import { Link } from 'react-router-dom'
import api from '../../api/axios.js'
import { Badge, Empty, PageTitle, Stat, fmtTime, useLoad } from '../../components/ui.jsx'

export default function FacultyDashboard() {
  const [d] = useLoad(() => api.get('/faculty/dashboard'))
  return (
    <>
      <PageTitle>Dashboard</PageTitle>
      {d && (<>
        <div className="row g-3 mb-4">
          <div className="col-md-4"><Stat label="Assigned courses" value={d.assignedCourses} /></div>
          <div className="col-md-4"><Stat label="Enrolled students" value={d.totalStudents} /></div>
          <div className="col-md-4"><div className="stat">
            <div className="num" style={{ fontSize: '1.3rem' }}>{d.todayStatus === 'NOT_MARKED' ? <span className="text-danger">Not marked</span> : <Badge value={d.todayStatus} />}</div>
            <div className="lbl">Your attendance today {d.todayStatus === 'NOT_MARKED' && <Link to="/faculty/my-attendance">· mark now</Link>}</div></div></div>
        </div>
        <div className="panel"><h5>Today's classes</h5>
          {d.todayClasses.length ? d.todayClasses.map((t) => (
            <div className="slot" key={t.id}><strong>{fmtTime(t.startTime)}–{fmtTime(t.endTime)}</strong> {t.courseCode} · {t.courseName}
              <div className="text-muted small">{t.room}</div></div>)) : <Empty>No classes today.</Empty>}
        </div>
      </>)}
    </>
  )
}
