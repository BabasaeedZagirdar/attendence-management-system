import { DAYS, Empty, cap, fmtTime } from './ui.jsx'

/** Week view grouped by day (Mon-Fri; Sat/Sun are holidays). */
export default function Timetable({ entries, onDelete }) {
  if (!entries?.length) return <Empty>No classes are scheduled yet.</Empty>
  return (
    <div className="row g-3">
      {DAYS.map((d) => (
        <div className="col-md-6 col-xl week-col" key={d}>
          <h6>{cap(d)}</h6>
          {entries.filter((e) => e.dayOfWeek === d).map((e) => (
            <div className="slot" key={e.id}>
              <strong>{fmtTime(e.startTime)}–{fmtTime(e.endTime)}</strong> {e.courseCode}<br />
              {e.courseName}
              <div className="text-muted small">{[e.room, e.facultyName].filter(Boolean).join(' · ')}</div>
              {onDelete && <button className="btn btn-sm btn-link text-danger p-0" onClick={() => onDelete(e.id)}>Remove</button>}
            </div>
          ))}
          {!entries.some((e) => e.dayOfWeek === d) && <div className="text-muted small">No classes</div>}
        </div>
      ))}
    </div>
  )
}
