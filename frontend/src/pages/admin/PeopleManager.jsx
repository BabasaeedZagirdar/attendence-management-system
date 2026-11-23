import { useState } from 'react'
import api, { errMsg } from '../../api/axios.js'
import { Badge, Empty, Modal, Msg, PageTitle, useLoad } from '../../components/ui.jsx'

const CFG = {
  students: { title: 'Students', one: 'student', idLabel: 'Roll number', idKey: 'rollNo' },
  faculty: { title: 'Faculty', one: 'faculty member', idLabel: 'Employee ID', idKey: 'employeeId' },
}
const blank = { name: '', email: '', password: '', phone: '', identifier: '', department: '' }

export default function PeopleManager({ kind }) {
  const cfg = CFG[kind]
  const [rows, reload, err] = useLoad(() => api.get(`/admin/${kind}`), [kind])
  const [edit, setEdit] = useState(null) // null | {} (new) | person
  const [form, setForm] = useState(blank)
  const [msg, setMsg] = useState(null)
  const [q, setQ] = useState('')

  const open = (p) => {
    setEdit(p || {})
    setForm(p ? { ...p, password: '' } : blank)
  }
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const save = async (e) => {
    e.preventDefault()
    const body = { name: form.name, email: form.email, password: form.password, phone: form.phone,
                   department: form.department, [cfg.idKey]: form.identifier }
    try {
      if (edit.id) await api.put(`/admin/${kind}/${edit.id}`, body)
      else await api.post(`/admin/${kind}`, body)
      setEdit(null); reload()
      setMsg({ type: 'success', text: `${cfg.one[0].toUpperCase() + cfg.one.slice(1)} saved` })
    } catch (e2) { setMsg({ type: 'danger', text: errMsg(e2) }) }
  }

  const toggle = async (p) => {
    try { await api.patch(`/admin/${kind}/${p.id}/status`, null, { params: { active: p.status !== 'ACTIVE' } }); reload() }
    catch (e2) { setMsg({ type: 'danger', text: errMsg(e2) }) }
  }

  const list = (rows || []).filter((p) => `${p.name} ${p.email} ${p.identifier}`.toLowerCase().includes(q.toLowerCase()))

  return (
    <>
      <PageTitle right={<button className="btn btn-primary" onClick={() => open()}>Add {cfg.one}</button>}>{cfg.title}</PageTitle>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      {err && <Msg msg={{ type: 'danger', text: errMsg(err) }} />}
      <input className="form-control mb-3" style={{ maxWidth: 360 }} placeholder="Search by name, email or ID" value={q} onChange={(e) => setQ(e.target.value)} />
      <div className="panel table-wrap p-0">
        <table className="table table-hover mb-0 align-middle">
          <thead><tr><th>{cfg.idLabel}</th><th>Name</th><th>Email</th><th>Department</th><th>Status</th><th /></tr></thead>
          <tbody>
            {list.map((p) => (
              <tr key={p.id}>
                <td>{p.identifier}</td><td>{p.name}</td><td>{p.email}</td><td>{p.department}</td>
                <td><Badge value={p.status} /></td>
                <td className="text-end text-nowrap">
                  <button className="btn btn-sm btn-outline-primary me-1" onClick={() => open(p)}>Edit</button>
                  <button className={`btn btn-sm ${p.status === 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}`} onClick={() => toggle(p)}>
                    {p.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {rows && !list.length && <Empty>No {cfg.title.toLowerCase()} found. Use "Add {cfg.one}" to create one.</Empty>}
      </div>

      {edit && (
        <Modal title={edit.id ? `Edit ${cfg.one}` : `Add ${cfg.one}`} onClose={() => setEdit(null)}>
          <form onSubmit={save} className="d-grid gap-2">
            <input className="form-control" placeholder="Full name" required value={form.name} onChange={set('name')} />
            <input className="form-control" type="email" placeholder="Email" required value={form.email} onChange={set('email')} />
            <input className="form-control" type="password" minLength={edit.id ? 0 : 6} required={!edit.id}
                   placeholder={edit.id ? 'New password (leave blank to keep)' : 'Password (min 6 characters)'} value={form.password} onChange={set('password')} />
            <input className="form-control" placeholder={cfg.idLabel} required value={form.identifier} onChange={set('identifier')} />
            <input className="form-control" placeholder="Department" value={form.department || ''} onChange={set('department')} />
            <input className="form-control" placeholder="Phone" value={form.phone || ''} onChange={set('phone')} />
            <button className="btn btn-primary mt-2">Save</button>
          </form>
        </Modal>
      )}
    </>
  )
}
