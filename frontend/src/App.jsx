import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import Home from './pages/Home.jsx'
import Login from './pages/Login.jsx'
import Signup from './pages/Signup.jsx'
import { ForgotPassword, ResetPassword } from './pages/PasswordPages.jsx'
import Profile from './pages/Profile.jsx'
import AdminDashboard from './pages/admin/AdminDashboard.jsx'
import PeopleManager from './pages/admin/PeopleManager.jsx'
import AdminCourses from './pages/admin/AdminCourses.jsx'
import AdminEnrolments from './pages/admin/AdminEnrolments.jsx'
import AdminTimetable from './pages/admin/AdminTimetable.jsx'
import AdminAttendance from './pages/admin/AdminAttendance.jsx'
import AdminReports from './pages/admin/AdminReports.jsx'
import StudentDashboard from './pages/student/StudentDashboard.jsx'
import StudentCourses from './pages/student/StudentCourses.jsx'
import StudentAttendance from './pages/student/StudentAttendance.jsx'
import FacultyDashboard from './pages/faculty/FacultyDashboard.jsx'
import FacultyCourses from './pages/faculty/FacultyCourses.jsx'
import MarkAttendance from './pages/faculty/MarkAttendance.jsx'
import MyAttendance from './pages/faculty/MyAttendance.jsx'
import FacultyReports from './pages/faculty/FacultyReports.jsx'
import TimetableView from './pages/TimetableView.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />

      <Route element={<ProtectedRoute role="ADMIN" />}>
        <Route path="/admin" element={<Layout />}>
          <Route index element={<AdminDashboard />} />
          <Route path="students" element={<PeopleManager kind="students" />} />
          <Route path="faculty" element={<PeopleManager kind="faculty" />} />
          <Route path="courses" element={<AdminCourses />} />
          <Route path="enrolments" element={<AdminEnrolments />} />
          <Route path="timetable" element={<AdminTimetable />} />
          <Route path="attendance" element={<AdminAttendance />} />
          <Route path="reports" element={<AdminReports />} />
          <Route path="profile" element={<Profile />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute role="STUDENT" />}>
        <Route path="/student" element={<Layout />}>
          <Route index element={<StudentDashboard />} />
          <Route path="courses" element={<StudentCourses />} />
          <Route path="attendance" element={<StudentAttendance />} />
          <Route path="timetable" element={<TimetableView endpoint="/student/timetable" />} />
          <Route path="profile" element={<Profile />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute role="FACULTY" />}>
        <Route path="/faculty" element={<Layout />}>
          <Route index element={<FacultyDashboard />} />
          <Route path="courses" element={<FacultyCourses />} />
          <Route path="mark-attendance" element={<MarkAttendance />} />
          <Route path="my-attendance" element={<MyAttendance />} />
          <Route path="reports" element={<FacultyReports />} />
          <Route path="timetable" element={<TimetableView endpoint="/faculty/timetable" />} />
          <Route path="profile" element={<Profile />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
