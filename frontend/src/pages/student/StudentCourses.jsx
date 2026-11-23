import { useState } from 'react'
import api, { errMsg } from '../../api/axios.js'
import { Empty, Msg, PageTitle, useLoad } from '../../components/ui.jsx'

export default function StudentCourses() {
  const [all, reloadAll] = useLoad(() => api.get('/student/courses/available'))
  const [msg, setMsg] = useState(null)
  const mine = (all || []).filter((c) => c.enrolled)
  const open = (all || []).filter((c) => !c.enrolled)

  const enrol = async (c) => {
    try { await api.post(`/student/courses/${c.id}/enrol`); setMsg({ type: 'success', text: `Enrolled in ${c.courseName}` }); reloadAll() }
    catch (e) { setMsg({ type: 'danger', text: errMsg(e) }) }
  }

  return (
    <>
      <PageTitle>Courses</PageTitle>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      <h5>My enrolled courses</h5>
      <div className="panel table-wrap p-0">
        <table className="table mb-0">
          <thead><tr><th>Code</th><th>Course</th><th>Credits</th><th>Faculty</th></tr></thead>
          <tbody>{mine.map((c) => <tr key={c.id}><td>{c.courseCode}</td><td>{c.courseName}</td><td>{c.credits}</td><td>{c.facultyName || 'Not assigned yet'}</td></tr>)}</tbody>
        </table>
        {all && !mine.length && <Empty>You are not enrolled in any course yet.</Empty>}
      </div>
      <h5>Available courses</h5>
      <div className="panel table-wrap p-0">
        <table className="table mb-0 align-middle">
          <thead><tr><th>Code</th><th>Course</th><th>Credits</th><th>Faculty</th><th /></tr></thead>
          <tbody>{open.map((c) => (
            <tr key={c.id}><td>{c.courseCode}</td><td>{c.courseName}</td><td>{c.credits}</td><td>{c.facultyName || 'Not assigned yet'}</td>
              <td className="text-end"><button className="btn btn-sm btn-primary" onClick={() => enrol(c)}>Enrol</button></td></tr>))}
          </tbody>
        </table>
        {all && !open.length && <Empty>No more courses are open for enrolment.</Empty>}
      </div>
    </>
  )
}
