import { useState } from 'react'
import api, { errMsg } from '../../api/axios.js'
import { Badge, Empty, Modal, Msg, PageTitle, useLoad } from '../../components/ui.jsx'

const blank = { courseName: '', courseCode: '', credits: 3, sessionId: '' }

export default function AdminCourses() {
  const [courses, reload] = useLoad(() => api.get('/admin/courses'))
  const [faculty] = useLoad(() => api.get('/admin/faculty'))
  const [sessions, reloadSessions] = useLoad(() => api.get('/admin/sessions'))
  const [edit, setEdit] = useState(null)
  const [form, setForm] = useState(blank)
  const [sess, setSess] = useState(null)
  const [msg, setMsg] = useState(null)
  const fail = (e) => setMsg({ type: 'danger', text: errMsg(e) })

  const open = (c) => {
    setEdit(c || {})
    setForm(c ? { courseName: c.courseName, courseCode: c.courseCode, credits: c.credits,
                  sessionId: sessions?.find((s) => s.name === c.sessionName)?.id || '' } : blank)
  }
  const save = async (e) => {
    e.preventDefault()
    const body = { ...form, credits: Number(form.credits), sessionId: form.sessionId ? Number(form.sessionId) : null }
    try {
      if (edit.id) await api.put(`/admin/courses/${edit.id}`, body); else await api.post('/admin/courses', body)
      setEdit(null); reload()
    } catch (e2) { fail(e2) }
  }
  const assign = async (course, facultyId) => {
    try {
      if (facultyId) await api.put(`/admin/courses/${course.id}/faculty/${facultyId}`)
      else await api.delete(`/admin/courses/${course.id}/faculty`)
      reload()
    } catch (e2) { fail(e2) }
  }
  const toggle = async (c) => {
    try { await api.patch(`/admin/courses/${c.id}/status`, null, { params: { active: !c.active } }); reload() } catch (e2) { fail(e2) }
  }
  const addSession = async (e) => {
    e.preventDefault()
    try { await api.post('/admin/sessions', { ...sess, current: !!sess.current }); setSess(null); reloadSessions() } catch (e2) { fail(e2) }
  }

  return (
    <>
      <PageTitle right={<div className="d-flex gap-2">
        <button className="btn btn-outline-primary" onClick={() => setSess({ name: '', startDate: '', endDate: '', current: true })}>Add academic session</button>
        <button className="btn btn-primary" onClick={() => open()}>Add course</button></div>}>Courses</PageTitle>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      <div className="panel table-wrap p-0">
        <table className="table table-hover mb-0 align-middle">
          <thead><tr><th>Code</th><th>Course</th><th>Credits</th><th>Session</th><th>Assigned faculty</th><th>Status</th><th /></tr></thead>
          <tbody>
            {(courses || []).map((c) => (
              <tr key={c.id}>
                <td>{c.courseCode}</td><td>{c.courseName}</td><td>{c.credits}</td><td>{c.sessionName || '—'}</td>
                <td style={{ minWidth: 200 }}>
                  <select className="form-select form-select-sm" value={c.facultyId || ''} onChange={(e) => assign(c, e.target.value)}>
                    <option value="">Not assigned</option>
                    {(faculty || []).filter((f) => f.status === 'ACTIVE' || f.id === c.facultyId).map((f) => <option key={f.id} value={f.id}>{f.name}</option>)}
                  </select>
                </td>
                <td><Badge value={c.active ? 'ACTIVE' : 'INACTIVE'} /></td>
                <td className="text-end text-nowrap">
                  <button className="btn btn-sm btn-outline-primary me-1" onClick={() => open(c)}>Edit</button>
                  <button className="btn btn-sm btn-outline-secondary" onClick={() => toggle(c)}>{c.active ? 'Close' : 'Reopen'}</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {courses && !courses.length && <Empty>No courses yet. Add your first course.</Empty>}
      </div>

      {edit && (
        <Modal title={edit.id ? 'Edit course' : 'Add course'} onClose={() => setEdit(null)}>
          <form onSubmit={save} className="d-grid gap-2">
            <input className="form-control" placeholder="Course name" required value={form.courseName} onChange={(e) => setForm({ ...form, courseName: e.target.value })} />
            <input className="form-control" placeholder="Course code (e.g. CS101)" required value={form.courseCode} onChange={(e) => setForm({ ...form, courseCode: e.target.value })} />
            <input className="form-control" type="number" min="1" max="10" placeholder="Credits" required value={form.credits} onChange={(e) => setForm({ ...form, credits: e.target.value })} />
            <select className="form-select" value={form.sessionId} onChange={(e) => setForm({ ...form, sessionId: e.target.value })}>
              <option value="">No academic session</option>
              {(sessions || []).map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
            <button className="btn btn-primary mt-2">Save</button>
          </form>
        </Modal>
      )}
      {sess && (
        <Modal title="Add academic session" onClose={() => setSess(null)}>
          <form onSubmit={addSession} className="d-grid gap-2">
            <input className="form-control" placeholder="Name (e.g. 2026-27 Semester 2)" required value={sess.name} onChange={(e) => setSess({ ...sess, name: e.target.value })} />
            <label className="small text-muted">Start date</label>
            <input className="form-control" type="date" value={sess.startDate} onChange={(e) => setSess({ ...sess, startDate: e.target.value || null })} />
            <label className="small text-muted">End date</label>
            <input className="form-control" type="date" value={sess.endDate} onChange={(e) => setSess({ ...sess, endDate: e.target.value || null })} />
            <div className="form-check"><input id="cur" className="form-check-input" type="checkbox" checked={sess.current} onChange={(e) => setSess({ ...sess, current: e.target.checked })} />
              <label htmlFor="cur" className="form-check-label">Current session</label></div>
            <button className="btn btn-primary mt-2">Save</button>
          </form>
        </Modal>
      )}
    </>
  )
}
