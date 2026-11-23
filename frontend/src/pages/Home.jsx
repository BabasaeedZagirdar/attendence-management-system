import { Link, Navigate } from 'react-router-dom'
import { useAuth, homeFor } from '../context/AuthContext.jsx'

export default function Home() {
  const { user } = useAuth()
  if (user) return <Navigate to={homeFor(user.role)} replace />
  return (
    <div className="hero">
      <div className="brand text-muted mb-3">Campus Register</div>
      <h1>One register for every class, student and faculty member.</h1>
      <p className="lead mt-3" style={{ maxWidth: 560 }}>
        Faculty take attendance for the courses they teach, students follow their own record,
        and the administration sees the whole picture. Weekends are holidays automatically.
      </p>
      <div className="d-flex gap-2 mt-4">
        <Link className="btn btn-primary btn-lg" to="/login">Log in</Link>
        <Link className="btn btn-outline-primary btn-lg" to="/signup">Create student account</Link>
      </div>
      <div className="role-grid">
        <div><h5>Administrators</h5><div className="text-muted">Manage accounts, courses, enrolments and timetables. View every attendance report.</div></div>
        <div><h5>Faculty</h5><div className="text-muted">Mark attendance for assigned courses, record your own day, and review reports.</div></div>
        <div><h5>Students</h5><div className="text-muted">Enrol in courses, see your faculty and timetable, and track your attendance.</div></div>
      </div>
    </div>
  )
}
