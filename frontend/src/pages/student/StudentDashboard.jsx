import api from '../../api/axios.js'
import { Empty, PageTitle, Stat, fmtTime, useLoad } from '../../components/ui.jsx'

export default function StudentDashboard() {
  const [d] = useLoad(() => api.get('/student/dashboard'))
  const weekend = [0, 6].includes(new Date().getDay())
  return (
    <>
      <PageTitle>Dashboard</PageTitle>
      {d && (<>
        <div className="row g-3 mb-4">
          <div className="col-md-4"><Stat label="Enrolled courses" value={d.enrolledCourses} /></div>
          <div className="col-md-4"><Stat label="Overall attendance" value={`${d.overallAttendance}%`} /></div>
          <div className="col-md-4"><Stat label="Classes attended" value={d.classesAttended} /></div>
        </div>
        <div className="panel">
          <h5>Today's classes</h5>
          {weekend ? <Empty>Today is a weekend holiday.</Empty>
            : d.todayClasses.length ? d.todayClasses.map((t) => (
              <div className="slot" key={t.id}><strong>{fmtTime(t.startTime)}–{fmtTime(t.endTime)}</strong> {t.courseCode} · {t.courseName}
                <div className="text-muted small">{[t.room, t.facultyName].filter(Boolean).join(' · ')}</div></div>))
            : <Empty>No classes scheduled today.</Empty>}
        </div>
      </>)}
    </>
  )
}
