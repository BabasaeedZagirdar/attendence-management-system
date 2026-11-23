import api from '../api/axios.js'
import Timetable from '../components/Timetable.jsx'
import { PageTitle, useLoad } from '../components/ui.jsx'

export default function TimetableView({ endpoint }) {
  const [rows] = useLoad(() => api.get(endpoint), [endpoint])
  return (
    <>
      <PageTitle>Timetable</PageTitle>
      <div className="panel">
        <Timetable entries={rows} />
        <div className="text-muted small mt-3">Saturday and Sunday are holidays.</div>
      </div>
    </>
  )
}
