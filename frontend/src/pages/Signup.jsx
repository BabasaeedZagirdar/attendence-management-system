import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api, { errMsg } from '../api/axios.js'
import { useAuth } from '../context/AuthContext.jsx'
import { Msg } from '../components/ui.jsx'

export default function Signup() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [f, setF] = useState({ name: '', email: '', password: '', rollNo: '', department: '', phone: '' })
  const [msg, setMsg] = useState(null)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    try {
      const res = await api.post('/auth/register', f)
      login(res.data)
      navigate('/student')
    } catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <Link to="/" className="brand text-decoration-none text-muted">Campus Register</Link>
        <h2 className="mt-2 mb-1">Student sign up</h2>
        <p className="text-muted small">Faculty accounts are created by the administrator.</p>
        <Msg msg={msg} />
        <form onSubmit={submit} className="d-grid gap-2">
          <input className="form-control" placeholder="Full name" required value={f.name} onChange={set('name')} />
          <input className="form-control" type="email" placeholder="Email" required value={f.email} onChange={set('email')} />
          <input className="form-control" type="password" placeholder="Password (min 6 characters)" minLength={6} required value={f.password} onChange={set('password')} />
          <input className="form-control" placeholder="Roll number" required value={f.rollNo} onChange={set('rollNo')} />
          <input className="form-control" placeholder="Department" value={f.department} onChange={set('department')} />
          <input className="form-control" placeholder="Phone (optional)" value={f.phone} onChange={set('phone')} />
          <button className="btn btn-primary mt-2">Create account</button>
        </form>
        <div className="mt-3 small">Already registered? <Link to="/login">Log in</Link></div>
      </div>
    </div>
  )
}
