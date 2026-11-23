import { useEffect, useState } from 'react'

export const Badge = ({ value }) => <span className={`badge badge-${value}`}>{value}</span>

export function Modal({ title, onClose, children }) {
  return (
    <>
      <div className="modal d-block" tabIndex="-1" role="dialog">
        <div className="modal-dialog"><div className="modal-content">
          <div className="modal-header"><h5 className="modal-title">{title}</h5>
            <button className="btn-close" aria-label="Close" onClick={onClose} /></div>
          <div className="modal-body">{children}</div>
        </div></div>
      </div>
      <div className="modal-backdrop show" />
    </>
  )
}

/** Shows an error or success message; pass { type: 'danger' | 'success', text } */
export const Msg = ({ msg, onClose }) =>
  msg ? (
    <div className={`alert alert-${msg.type} alert-dismissible`} role="alert">
      {msg.text}
      {onClose && <button type="button" className="btn-close" onClick={onClose} />}
    </div>
  ) : null

export const Empty = ({ children }) => <div className="text-muted py-3">{children}</div>

export const PageTitle = ({ children, right }) => (
  <div className="d-flex justify-content-between align-items-center page-title flex-wrap gap-2">
    <h2 className="m-0">{children}</h2>{right}
  </div>
)

export const Stat = ({ label, value }) => (
  <div className="stat"><div className="num">{value}</div><div className="lbl">{label}</div></div>
)

export const Percent = ({ value }) => (
  <div className="d-flex align-items-center gap-2" style={{ minWidth: 140 }}>
    <div className="progress flex-grow-1"><div className={`progress-bar ${value < 75 ? 'low' : ''}`} style={{ width: `${value}%` }} /></div>
    <span className="small">{value}%</span>
  </div>
)

export const fmtTime = (t) => (t ? t.slice(0, 5) : '')
export const today = () => new Date().toISOString().slice(0, 10)
export const isWeekend = (iso) => { const d = new Date(iso + 'T00:00:00').getDay(); return d === 0 || d === 6 }
export const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY']
export const cap = (s) => s.charAt(0) + s.slice(1).toLowerCase()

/** Loads data from an API call; returns [data, reload, error]. */
export function useLoad(fn, deps = []) {
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const reload = () => fn().then((r) => { setData(r.data); setError(null) }).catch((e) => setError(e))
  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => { reload() }, deps)
  return [data, reload, error]
}
