import { useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import api, { errMsg } from '../api/axios.js'
import { Msg } from '../components/ui.jsx'

export function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [msg, setMsg] = useState(null)
  const submit = async (e) => {
    e.preventDefault()
    try { const r = await api.post('/auth/forgot-password', { email }); setMsg({ type: 'success', text: r.data.message }) }
    catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }
  return (
    <div className="auth-wrap"><div className="auth-card">
      <h2 className="mb-3">Reset your password</h2>
      <Msg msg={msg} />
      <form onSubmit={submit}>
        <input className="form-control mb-3" type="email" placeholder="Your registered email" required value={email} onChange={(e) => setEmail(e.target.value)} />
        <button className="btn btn-primary w-100">Send reset link</button>
      </form>
      <div className="mt-3 small"><Link to="/login">Back to log in</Link></div>
    </div></div>
  )
}

export function ResetPassword() {
  const [params] = useSearchParams()
  const [pw, setPw] = useState('')
  const [msg, setMsg] = useState(null)
  const submit = async (e) => {
    e.preventDefault()
    try { const r = await api.post('/auth/reset-password', { token: params.get('token'), newPassword: pw }); setMsg({ type: 'success', text: r.data.message }) }
    catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }
  return (
    <div className="auth-wrap"><div className="auth-card">
      <h2 className="mb-3">Choose a new password</h2>
      <Msg msg={msg} />
      <form onSubmit={submit}>
        <input className="form-control mb-3" type="password" placeholder="New password (min 6 characters)" minLength={6} required value={pw} onChange={(e) => setPw(e.target.value)} />
        <button className="btn btn-primary w-100">Update password</button>
      </form>
      <div className="mt-3 small"><Link to="/login">Back to log in</Link></div>
    </div></div>
  )
}
