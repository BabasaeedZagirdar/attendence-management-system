import { useState } from 'react'
import api from '../../api/axios.js'
import { Empty, PageTitle, Percent, useLoad } from '../../components/ui.jsx'

export default function FacultyReports() {
  const [courses] = useLoad(() => api.get('/faculty/courses'))
  const [courseId, setCourseId] = useState('')
  const [rows, setRows] = useState(null)

  const pick = async (id) => {
    setCourseId(id)
    setRows(id ? (await api.get('/faculty/reports', { params: { courseId: id } })).data : null)
  }
  return (
    <>
      <PageTitle>Attendance reports</PageTitle>
      <div className="panel"><label className="form-label">Course</label>
        <select className="form-select" style={{ maxWidth: 420 }} value={courseId} onChange={(e) => pick(e.target.value)}>
          <option value="">Select a course</option>{(courses || []).map((c) => <option key={c.id} value={c.id}>{c.courseCode} · {c.courseName}</option>)}</select></div>
      {rows && (
        <div className="panel table-wrap p-0">
          <table className="table mb-0 align-middle">
            <thead><tr><th>Roll no</th><th>Student</th><th>Classes</th><th>Present</th><th>Absent</th><th>Leave</th><th>Attendance</th></tr></thead>
            <tbody>{rows.map((r) => <tr key={r.id}><td>{r.code}</td><td>{r.name}</td><td>{r.total}</td><td>{r.present}</td><td>{r.absent}</td><td>{r.leave}</td><td><Percent value={r.percentage} /></td></tr>)}</tbody>
          </table>
          {!rows.length && <Empty>No students are enrolled in this course.</Empty>}
        </div>)}
    </>
  )
}
