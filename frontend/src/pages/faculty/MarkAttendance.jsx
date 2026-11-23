import { useEffect, useState } from 'react'
import api, { errMsg } from '../../api/axios.js'
import { Empty, Msg, PageTitle, isWeekend, today, useLoad } from '../../components/ui.jsx'

const OPTIONS = [['PRESENT', 'Present'], ['ABSENT', 'Absent'], ['LEAVE', 'Leave']]

export default function MarkAttendance() {
  const [courses] = useLoad(() => api.get('/faculty/courses'))
  const [courseId, setCourseId] = useState('')
  const [date, setDate] = useState(today())
  const [students, setStudents] = useState([])
  const [status, setStatus] = useState({})
  const [msg, setMsg] = useState(null)
  const holiday = isWeekend(date)

  useEffect(() => {
    if (!courseId) { setStudents([]); return }
    Promise.all([
      api.get(`/faculty/courses/${courseId}/students`),
      holiday ? Promise.resolve({ data: [] }) : api.get('/faculty/attendance', { params: { courseId, date } }),
    ]).then(([s, existing]) => {
      setStudents(s.data)
      const saved = Object.fromEntries(existing.data.map((r) => [r.studentId, r.status]))
      setStatus(Object.fromEntries(s.data.map((x) => [x.id, saved[x.id] || 'PRESENT'])))
      setMsg(existing.data.length ? { type: 'info', text: 'Attendance was already marked for this date. Saving will update it.' } : null)
    }).catch((e) => setMsg({ type: 'danger', text: errMsg(e) }))
  }, [courseId, date, holiday])

  const setAll = (v) => setStatus(Object.fromEntries(students.map((s) => [s.id, v])))
  const save = async () => {
    try {
      const records = students.map((s) => ({ studentId: s.id, status: status[s.id] }))
      const r = await api.post('/faculty/attendance', { courseId: Number(courseId), date, records })
      setMsg({ type: 'success', text: r.data.message })
    } catch (e) { setMsg({ type: 'danger', text: errMsg(e) }) }
  }

  return (
    <>
      <PageTitle>Mark attendance</PageTitle>
      <div className="panel row g-2">
        <div className="col-md-7"><label className="form-label">Course</label>
          <select className="form-select" value={courseId} onChange={(e) => setCourseId(e.target.value)}>
            <option value="">Select a course</option>{(courses || []).map((c) => <option key={c.id} value={c.id}>{c.courseCode} · {c.courseName}</option>)}</select></div>
        <div className="col-md-5"><label className="form-label">Class date</label>
          <input className="form-control" type="date" max={today()} value={date} onChange={(e) => setDate(e.target.value)} /></div>
      </div>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      {holiday && <div className="alert alert-secondary">{date} is a weekend. Saturdays and Sundays are holidays, so attendance cannot be marked.</div>}

      {courseId && !holiday && (
        <div className="panel p-0">
          {students.length ? (<>
            <div className="p-3 d-flex gap-2 align-items-center border-bottom">
              <span className="me-auto">{students.length} students</span>
              {OPTIONS.map(([v, l]) => <button key={v} className="btn btn-sm btn-outline-secondary" onClick={() => setAll(v)}>All {l.toLowerCase()}</button>)}
            </div>
            <div className="table-wrap"><table className="table mb-0 align-middle">
              <thead><tr><th>Roll no</th><th>Student</th><th>Status</th></tr></thead>
              <tbody>{students.map((s) => (
                <tr key={s.id}><td>{s.identifier}</td><td>{s.name}</td>
                  <td><div className="btn-group btn-group-sm">
                    {OPTIONS.map(([v, l]) => (
                      <button key={v} type="button" className={`btn ${status[s.id] === v ? 'btn-primary' : 'btn-outline-secondary'}`}
                              onClick={() => setStatus({ ...status, [s.id]: v })}>{l}</button>))}
                  </div></td></tr>))}
              </tbody></table></div>
            <div className="p-3"><button className="btn btn-primary" onClick={save}>Save attendance</button></div>
          </>) : <Empty>No students are enrolled in this course yet.</Empty>}
        </div>
      )}
    </>
  )
}
