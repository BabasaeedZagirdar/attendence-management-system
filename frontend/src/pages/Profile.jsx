import { useEffect, useState } from 'react'
import api, { errMsg } from '../api/axios.js'
import { useAuth } from '../context/AuthContext.jsx'
import { Msg, PageTitle } from '../components/ui.jsx'

export default function Profile() {
  const { user, login } = useAuth()
  const [p, setP] = useState(null)
  const [msg, setMsg] = useState(null)

  useEffect(() => { api.get('/profile').then((r) => setP(r.data)).catch((e) => setMsg({ type: 'danger', text: errMsg(e) })) }, [])
  if (!p) return <Msg msg={msg} />

  const save = async (e) => {
    e.preventDefault()
    try {
      const r = await api.put('/profile', { name: p.name, phone: p.phone })
      setP(r.data)
      login({ token: localStorage.getItem('token'), user: { ...user, name: r.data.name } })
      setMsg({ type: 'success', text: 'Profile updated' })
    } catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }
  const ro = (label, v) => (
    <div className="col-md-6"><label className="form-label">{label}</label><input className="form-control" value={v || '—'} disabled /></div>
  )

  return (
    <>
      <PageTitle>My profile</PageTitle>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      <form className="panel" onSubmit={save} style={{ maxWidth: 720 }}>
        <div className="row g-3">
          <div className="col-md-6"><label className="form-label">Full name</label>
            <input className="form-control" required value={p.name} onChange={(e) => setP({ ...p, name: e.target.value })} /></div>
          <div className="col-md-6"><label className="form-label">Phone</label>
            <input className="form-control" value={p.phone || ''} onChange={(e) => setP({ ...p, phone: e.target.value })} /></div>
          {ro('Email', p.email)}
          {ro('Role', p.role)}
          {p.role === 'STUDENT' && ro('Roll number', p.identifier)}
          {p.role === 'FACULTY' && ro('Employee ID', p.identifier)}
          {p.role !== 'ADMIN' && ro('Department', p.department)}
        </div>
        <button className="btn btn-primary mt-3">Save changes</button>
      </form>
    </>
  )
}
