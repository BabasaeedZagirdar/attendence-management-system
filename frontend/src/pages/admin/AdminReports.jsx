import api from '../../api/axios.js'
import { Empty, PageTitle, Percent, useLoad } from '../../components/ui.jsx'

export default function AdminReports() {
  const [rows] = useLoad(() => api.get('/admin/reports/courses'))
  return (
    <>
      <PageTitle>Attendance by course</PageTitle>
      <div className="panel table-wrap p-0">
        <table className="table mb-0 align-middle">
          <thead><tr><th>Code</th><th>Course</th><th>Records</th><th>Present</th><th>Absent</th><th>Leave</th><th>Attendance</th></tr></thead>
          <tbody>
            {(rows || []).map((r) => (
              <tr key={r.id}><td>{r.code}</td><td>{r.name}</td><td>{r.total}</td><td>{r.present}</td><td>{r.absent}</td><td>{r.leave}</td>
                <td><Percent value={r.percentage} /></td></tr>
            ))}
          </tbody>
        </table>
        {rows && !rows.length && <Empty>No courses yet.</Empty>}
      </div>
      <div className="text-muted small">Attendance % is present days divided by all marked days. Bars below 75% are shown in red.</div>
    </>
  )
}
