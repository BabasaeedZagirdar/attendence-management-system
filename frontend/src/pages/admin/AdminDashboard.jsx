import api from '../../api/axios.js'
import { PageTitle, Stat, useLoad } from '../../components/ui.jsx'

export default function AdminDashboard() {
  const [s] = useLoad(() => api.get('/admin/dashboard'))
  return (
    <>
      <PageTitle>Administration</PageTitle>
      {s && (
        <div className="row g-3">
          <div className="col-6 col-lg-4"><Stat label="Students" value={s.students} /></div>
          <div className="col-6 col-lg-4"><Stat label="Faculty" value={s.faculty} /></div>
          <div className="col-6 col-lg-4"><Stat label="Courses" value={s.courses} /></div>
          <div className="col-6 col-lg-4"><Stat label="Active enrolments" value={s.activeEnrolments} /></div>
          <div className="col-6 col-lg-4"><Stat label="Students present today" value={s.studentsPresentToday} /></div>
          <div className="col-6 col-lg-4"><Stat label="Faculty present today" value={s.facultyPresentToday} /></div>
        </div>
      )}
    </>
  )
}
