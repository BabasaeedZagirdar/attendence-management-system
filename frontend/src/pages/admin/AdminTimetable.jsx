import { useState } from 'react'
import api, { errMsg } from '../../api/axios.js'
import Timetable from '../../components/Timetable.jsx'
import { DAYS, Msg, PageTitle, cap, useLoad } from '../../components/ui.jsx'

export default function AdminTimetable() {
  const [rows, reload] = useLoad(() => api.get('/admin/timetable'))
  const [courses] = useLoad(() => api.get('/admin/courses'))
  const [f, setF] = useState({ courseId: '', dayOfWeek: 'MONDAY', startTime: '09:00', endTime: '10:00', room: '' })
  const [msg, setMsg] = useState(null)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  const add = async (e) => {
    e.preventDefault()
    try { await api.post('/admin/timetable', { ...f, courseId: Number(f.courseId) }); setMsg(null); reload() }
    catch (err) { setMsg({ type: 'danger', text: errMsg(err) }) }
  }
  const del = async (id) => { await api.delete(`/admin/timetable/${id}`); reload() }

  return (
    <>
      <PageTitle>Timetable</PageTitle>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      <form className="panel row g-2 align-items-end" onSubmit={add}>
        <div className="col-md-3"><label className="form-label">Course</label>
          <select className="form-select" required value={f.courseId} onChange={set('courseId')}>
            <option value="">Select course</option>
            {(courses || []).filter((c) => c.active).map((c) => <option key={c.id} value={c.id}>{c.courseCode} · {c.courseName}</option>)}
          </select></div>
        <div className="col-md-2"><label className="form-label">Day</label>
          <select className="form-select" value={f.dayOfWeek} onChange={set('dayOfWeek')}>{DAYS.map((d) => <option key={d} value={d}>{cap(d)}</option>)}</select></div>
        <div className="col-md-2"><label className="form-label">Start</label><input className="form-control" type="time" required value={f.startTime} onChange={set('startTime')} /></div>
        <div className="col-md-2"><label className="form-label">End</label><input className="form-control" type="time" required value={f.endTime} onChange={set('endTime')} /></div>
        <div className="col-md-2"><label className="form-label">Room</label><input className="form-control" value={f.room} onChange={set('room')} /></div>
        <div className="col-md-1"><button className="btn btn-primary w-100">Add</button></div>
      </form>
      <div className="panel"><Timetable entries={rows} onDelete={del} /></div>
    </>
  )
}
