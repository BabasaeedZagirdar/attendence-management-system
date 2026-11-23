import { useEffect, useRef, useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import api, { errMsg } from '../api/axios.js'
import { useAuth, homeFor } from '../context/AuthContext.jsx'
import { Msg } from '../components/ui.jsx'

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID

export default function Login() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [msg, setMsg] = useState(null)
  const [busy, setBusy] = useState(false)
  const gbtn = useRef(null)

  const done = (res) => { login(res.data); navigate(homeFor(res.data.user.role)) }

  useEffect(() => {
    if (!GOOGLE_CLIENT_ID) return
    const init = () => {
      if (!window.google || !gbtn.current) return false
      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: (r) => api.post('/auth/google', { idToken: r.credential }).then(done)
          .catch((e) => setMsg({ type: 'danger', text: errMsg(e) })),
      })
      window.google.accounts.id.renderButton(gbtn.current, { theme: 'outline', size: 'large', width: 340 })
      return true
    }
    if (init()) return
    const t = setInterval(() => init() && clearInterval(t), 300)
    return () => clearInterval(t)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  if (user) return <Navigate to={homeFor(user.role)} replace />

  const submit = async (e) => {
    e.preventDefault()
    setBusy(true); setMsg(null)
    try { done(await api.post('/auth/login', form)) }
    catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
    finally { setBusy(false) }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <Link to="/" className="brand text-decoration-none text-muted">Campus Register</Link>
        <h2 className="mt-2 mb-3">Log in</h2>
        <Msg msg={msg} />
        <form onSubmit={submit}>
          <label className="form-label">Email</label>
          <input className="form-control mb-3" type="email" required value={form.email}
                 onChange={(e) => setForm({ ...form, email: e.target.value })} autoFocus />
          <label className="form-label">Password</label>
          <input className="form-control mb-2" type="password" required value={form.password}
                 onChange={(e) => setForm({ ...form, password: e.target.value })} />
          <div className="mb-3"><Link to="/forgot-password" className="small">Forgot password?</Link></div>
          <button className="btn btn-primary w-100" disabled={busy}>{busy ? 'Logging in…' : 'Log in'}</button>
        </form>
        {GOOGLE_CLIENT_ID && (<><div className="text-center text-muted my-3">or</div><div ref={gbtn} className="d-flex justify-content-center" /></>)}
        <div className="mt-3 small">New student? <Link to="/signup">Create an account</Link></div>
      </div>
    </div>
  )
}
