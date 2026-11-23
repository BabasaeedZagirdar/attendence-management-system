import { useState } from 'react'
import api, { errMsg } from '../../api/axios.js'
import { Empty, Msg, PageTitle, useLoad } from '../../components/ui.jsx'

export default function AdminEnrolments() {
  const [rows, reload] = useLoad(() => api.get('/admin/enrolments'))
  const [students] = useLoad(() => api.get('/admin/students'))
  const [courses] = useLoad(() => api.get('/admin/courses'))
  const [f, setF] = useState({ studentId: '', courseId: '' })
  const [msg, setMsg] = useState(null)

  const add = async (e) => {
    e.preventDefault()
    try {
      await api.post('/admin/enrolments', { studentId: Number(f.studentId), courseId: Number(f.courseId) })
      setMsg({ type: 'success', text: 'Student enrolled' }); reload()
    } catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }
  const drop = async (r) => {
    if (!window.confirm(`Remove ${r.studentName} from ${r.courseCode}?`)) return
    try { await api.delete('/admin/enrolments', { params: { studentId: r.studentId, courseId: r.courseId } }); reload() }
    catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }

  return (
    <>
      <PageTitle>Course enrolments</PageTitle>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      <form className="panel row g-2 align-items-end" onSubmit={add}>
        <div className="col-md-5"><label className="form-label">Student</label>
          <select className="form-select" required value={f.studentId} onChange={(e) => setF({ ...f, studentId: e.target.value })}>
            <option value="">Select student</option>
            {(students || []).filter((s) => s.status === 'ACTIVE').map((s) => <option key={s.id} value={s.id}>{s.identifier} · {s.name}</option>)}
          </select></div>
        <div className="col-md-5"><label className="form-label">Course</label>
          <select className="form-select" required value={f.courseId} onChange={(e) => setF({ ...f, courseId: e.target.value })}>
            <option value="">Select course</option>
            {(courses || []).filter((c) => c.active).map((c) => <option key={c.id} value={c.id}>{c.courseCode} · {c.courseName}</option>)}
          </select></div>
        <div className="col-md-2"><button className="btn btn-primary w-100">Enrol</button></div>
      </form>
      <div className="panel table-wrap p-0">
        <table className="table table-hover mb-0 align-middle">
          <thead><tr><th>Roll no</th><th>Student</th><th>Course</th><th /></tr></thead>
          <tbody>
            {(rows || []).map((r) => (
              <tr key={r.id}><td>{r.rollNo}</td><td>{r.studentName}</td><td>{r.courseCode} · {r.courseName}</td>
                <td className="text-end"><button className="btn btn-sm btn-outline-danger" onClick={() => drop(r)}>Remove</button></td></tr>
            ))}
          </tbody>
        </table>
        {rows && !rows.length && <Empty>No enrolments yet.</Empty>}
      </div>
    </>
  )
}
