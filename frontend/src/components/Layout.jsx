import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

const MENU = {
  ADMIN: [
    ['/admin', 'Dashboard', true], ['/admin/students', 'Students'], ['/admin/faculty', 'Faculty'],
    ['/admin/courses', 'Courses'], ['/admin/enrolments', 'Enrolments'], ['/admin/timetable', 'Timetable'],
    ['/admin/attendance', 'Attendance'], ['/admin/reports', 'Reports'], ['/admin/profile', 'My profile'],
  ],
  STUDENT: [
    ['/student', 'Dashboard', true], ['/student/courses', 'Courses'], ['/student/attendance', 'My attendance'],
    ['/student/timetable', 'Timetable'], ['/student/profile', 'My profile'],
  ],
  FACULTY: [
    ['/faculty', 'Dashboard', true], ['/faculty/courses', 'My courses'], ['/faculty/mark-attendance', 'Mark attendance'],
    ['/faculty/my-attendance', 'My daily attendance'], ['/faculty/reports', 'Reports'],
    ['/faculty/timetable', 'Timetable'], ['/faculty/profile', 'My profile'],
  ],
}

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const out = () => { logout(); navigate('/login') }
  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">Campus Register</div>
        <div className="who">{user.name}<br />{user.role.toLowerCase()}</div>
        <nav className="nav flex-column">
          {MENU[user.role].map(([to, label, end]) => (
            <NavLink key={to} to={to} end={end} className="nav-link">{label}</NavLink>
          ))}
          <button className="nav-link text-start border-0 bg-transparent" onClick={out}>Log out</button>
        </nav>
      </aside>
      <main className="main"><Outlet /></main>
    </div>
  )
}
