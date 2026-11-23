import { useState } from 'react'
import api, { errMsg } from '../../api/axios.js'
import { Badge, Empty, Msg, PageTitle, isWeekend, today, useLoad } from '../../components/ui.jsx'

export default function AdminAttendance() {
  const [tab, setTab] = useState('students')
  const [courses] = useLoad(() => api.get('/admin/courses'))
  const [faculty] = useLoad(() => api.get('/admin/faculty'))
  const [flt, setFlt] = useState({ courseId: '', facultyId: '', from: '', to: '' })
  const [rows, setRows] = useState(null)
  const [mark, setMark] = useState({ facultyId: '', date: today(), status: 'PRESENT' })
  const [msg, setMsg] = useState(null)

  const clean = (o) => Object.fromEntries(Object.entries(o).filter(([, v]) => v !== ''))
  const search = async (e) => {
    e?.preventDefault()
    try {
      const params = tab === 'students'
        ? clean({ courseId: flt.courseId, from: flt.from, to: flt.to })
        : clean({ facultyId: flt.facultyId, from: flt.from, to: flt.to })
      setRows((await api.get(`/admin/attendance/${tab}`, { params })).data); setMsg(null)
    } catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }
  const switchTab = (t) => { setTab(t); setRows(null) }
  const saveFaculty = async (e) => {
    e.preventDefault()
    try {
      await api.post('/admin/attendance/faculty', { ...mark, facultyId: Number(mark.facultyId) })
      setMsg({ type: 'success', text: 'Faculty attendance saved' }); if (tab === 'faculty') search()
    } catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }

  return (
    <>
      <PageTitle>Attendance records</PageTitle>
      <ul className="nav nav-tabs mb-3">
        <li className="nav-item"><button className={`nav-link ${tab === 'students' ? 'active' : ''}`} onClick={() => switchTab('students')}>Students</button></li>
        <li className="nav-item"><button className={`nav-link ${tab === 'faculty' ? 'active' : ''}`} onClick={() => switchTab('faculty')}>Faculty</button></li>
      </ul>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      <form className="panel row g-2 align-items-end" onSubmit={search}>
        <div className="col-md-4">
          {tab === 'students' ? (<><label className="form-label">Course</label>
            <select className="form-select" value={flt.courseId} onChange={(e) => setFlt({ ...flt, courseId: e.target.value })}>
              <option value="">All courses</option>{(courses || []).map((c) => <option key={c.id} value={c.id}>{c.courseCode} · {c.courseName}</option>)}</select></>)
          : (<><label className="form-label">Faculty</label>
            <select className="form-select" value={flt.facultyId} onChange={(e) => setFlt({ ...flt, facultyId: e.target.value })}>
              <option value="">All faculty</option>{(faculty || []).map((f) => <option key={f.id} value={f.id}>{f.name}</option>)}</select></>)}
        </div>
        <div className="col-md-3"><label className="form-label">From</label><input className="form-control" type="date" value={flt.from} onChange={(e) => setFlt({ ...flt, from: e.target.value })} /></div>
        <div className="col-md-3"><label className="form-label">To</label><input className="form-control" type="date" value={flt.to} onChange={(e) => setFlt({ ...flt, to: e.target.value })} /></div>
        <div className="col-md-2"><button className="btn btn-primary w-100">Show records</button></div>
      </form>

      {tab === 'faculty' && (
        <form className="panel row g-2 align-items-end" onSubmit={saveFaculty}>
          <div className="col-12"><strong>Mark faculty attendance</strong></div>
          <div className="col-md-4"><select className="form-select" required value={mark.facultyId} onChange={(e) => setMark({ ...mark, facultyId: e.target.value })}>
            <option value="">Select faculty</option>{(faculty || []).map((f) => <option key={f.id} value={f.id}>{f.name}</option>)}</select></div>
          <div className="col-md-3"><input className="form-control" type="date" max={today()} required value={mark.date} onChange={(e) => setMark({ ...mark, date: e.target.value })} /></div>
          <div className="col-md-3"><select className="form-select" value={mark.status} onChange={(e) => setMark({ ...mark, status: e.target.value })}>
            <option>PRESENT</option><option>ABSENT</option><option>LEAVE</option></select></div>
          <div className="col-md-2"><button className="btn btn-primary w-100" disabled={isWeekend(mark.date)}>Save</button></div>
          {isWeekend(mark.date) && <div className="col-12 text-muted small">That date is a weekend holiday. Attendance cannot be marked.</div>}
        </form>
      )}

      {rows && (
        <div className="panel table-wrap p-0">
          <table className="table table-hover mb-0">
            {tab === 'students' ? (
              <><thead><tr><th>Date</th><th>Roll no</th><th>Student</th><th>Course</th><th>Status</th></tr></thead>
              <tbody>{rows.map((r) => <tr key={r.id}><td>{r.date}</td><td>{r.rollNo}</td><td>{r.studentName}</td><td>{r.courseName}</td><td><Badge value={r.status} /></td></tr>)}</tbody></>
            ) : (
              <><thead><tr><th>Date</th><th>Employee ID</th><th>Faculty</th><th>Status</th></tr></thead>
              <tbody>{rows.map((r) => <tr key={r.id}><td>{r.date}</td><td>{r.employeeId}</td><td>{r.facultyName}</td><td><Badge value={r.status} /></td></tr>)}</tbody></>
            )}
          </table>
          {!rows.length && <Empty>No attendance records match these filters.</Empty>}
        </div>
      )}
    </>
  )
}
