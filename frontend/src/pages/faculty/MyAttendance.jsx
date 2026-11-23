import { useState } from 'react'
import api, { errMsg } from '../../api/axios.js'
import { Badge, Empty, Msg, PageTitle, isWeekend, today, useLoad } from '../../components/ui.jsx'

export default function MyAttendance() {
  const [rows, reload] = useLoad(() => api.get('/faculty/my-attendance'))
  const [msg, setMsg] = useState(null)
  const holiday = isWeekend(today())
  const todayRow = (rows || []).find((r) => r.date === today())

  const mark = async (status) => {
    try { await api.post('/faculty/my-attendance', { status }); setMsg({ type: 'success', text: `Marked ${status.toLowerCase()} for today` }); reload() }
    catch (e) { setMsg({ type: 'danger', text: errMsg(e) }) }
  }

  return (
    <>
      <PageTitle>My daily attendance</PageTitle>
      <Msg msg={msg} onClose={() => setMsg(null)} />
      <div className="panel">
        <h5>Today · {today()}</h5>
        {holiday ? <div className="text-muted">Today is a weekend holiday. There is nothing to mark.</div> : (<>
          <div className="mb-2">Current status: {todayRow ? <Badge value={todayRow.status} /> : <span className="text-danger">Not marked</span>}</div>
          <div className="btn-group">
            <button className="btn btn-primary" onClick={() => mark('PRESENT')}>Present</button>
            <button className="btn btn-outline-secondary" onClick={() => mark('ABSENT')}>Absent</button>
            <button className="btn btn-outline-secondary" onClick={() => mark('LEAVE')}>Leave</button>
          </div></>)}
      </div>
      <h5>History</h5>
      <div className="panel table-wrap p-0">
        <table className="table mb-0"><thead><tr><th>Date</th><th>Status</th></tr></thead>
          <tbody>{(rows || []).map((r) => <tr key={r.id}><td>{r.date}</td><td><Badge value={r.status} /></td></tr>)}</tbody></table>
        {rows && !rows.length && <Empty>No attendance marked yet.</Empty>}
      </div>
    </>
  )
}
