import { useState } from 'react'
import api from '../../api/axios.js'
import { Badge, Empty, PageTitle, Percent, useLoad } from '../../components/ui.jsx'

export default function StudentAttendance() {
  const [sum] = useLoad(() => api.get('/student/attendance/summary'))
  const [courseId, setCourseId] = useState('')
  const [rows] = useLoad(() => api.get('/student/attendance', { params: courseId ? { courseId } : {} }), [courseId])

  return (
    <>
      <PageTitle>My attendance</PageTitle>
      <div className="panel table-wrap p-0">
        <table className="table mb-0 align-middle">
          <thead><tr><th>Course</th><th>Classes</th><th>Present</th><th>Absent</th><th>Leave</th><th>Attendance</th></tr></thead>
          <tbody>{(sum || []).map((s) => (
            <tr key={s.id}><td>{s.code} · {s.name}</td><td>{s.total}</td><td>{s.present}</td><td>{s.absent}</td><td>{s.leave}</td><td><Percent value={s.percentage} /></td></tr>))}
          </tbody>
        </table>
        {sum && !sum.length && <Empty>Enrol in a course to start tracking attendance.</Empty>}
      </div>

      <div className="d-flex justify-content-between align-items-center mb-2 flex-wrap gap-2">
        <h5 className="m-0">Daily record</h5>
        <select className="form-select" style={{ maxWidth: 300 }} value={courseId} onChange={(e) => setCourseId(e.target.value)}>
          <option value="">All courses</option>{(sum || []).map((s) => <option key={s.id} value={s.id}>{s.code} · {s.name}</option>)}
        </select>
      </div>
      <div className="panel table-wrap p-0">
        <table className="table mb-0">
          <thead><tr><th>Date</th><th>Course</th><th>Status</th></tr></thead>
          <tbody>{(rows || []).map((r) => <tr key={r.id}><td>{r.date}</td><td>{r.courseName}</td><td><Badge value={r.status} /></td></tr>)}</tbody>
        </table>
        {rows && !rows.length && <Empty>No attendance has been marked yet.</Empty>}
      </div>
      <div className="text-muted small">Saturdays and Sundays are holidays. Your faculty mark attendance on working days.</div>
    </>
  )
}
