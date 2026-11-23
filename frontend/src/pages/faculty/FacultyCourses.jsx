import { useState } from 'react'
import api from '../../api/axios.js'
import { Empty, PageTitle, useLoad } from '../../components/ui.jsx'

export default function FacultyCourses() {
  const [courses] = useLoad(() => api.get('/faculty/courses'))
  const [open, setOpen] = useState(null)
  const [students, setStudents] = useState([])

  const toggle = async (c) => {
    if (open === c.id) return setOpen(null)
    setStudents((await api.get(`/faculty/courses/${c.id}/students`)).data); setOpen(c.id)
  }
  return (
    <>
      <PageTitle>My courses</PageTitle>
      {(courses || []).map((c) => (
        <div className="panel" key={c.id}>
          <div className="d-flex justify-content-between align-items-center">
            <div><h5 className="m-0">{c.courseCode} · {c.courseName}</h5><div className="text-muted small">{c.credits} credits {c.sessionName && `· ${c.sessionName}`}</div></div>
            <button className="btn btn-outline-primary btn-sm" onClick={() => toggle(c)}>{open === c.id ? 'Hide students' : 'View enrolled students'}</button>
          </div>
          {open === c.id && (
            <div className="table-wrap mt-3">
              <table className="table table-sm mb-0"><thead><tr><th>Roll no</th><th>Name</th><th>Email</th><th>Department</th></tr></thead>
                <tbody>{students.map((s) => <tr key={s.id}><td>{s.identifier}</td><td>{s.name}</td><td>{s.email}</td><td>{s.department}</td></tr>)}</tbody></table>
              {!students.length && <Empty>No students are enrolled yet.</Empty>}
            </div>)}
        </div>
      ))}
      {courses && !courses.length && <Empty>No courses have been assigned to you yet. Contact the administrator.</Empty>}
    </>
  )
}
