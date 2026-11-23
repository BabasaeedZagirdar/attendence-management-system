import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    const onAuthPage = err.config?.url?.startsWith('/auth')
    if (err.response?.status === 401 && !onAuthPage) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  },
)

export const errMsg = (e) =>
  e?.response?.data?.message || (e?.response ? 'Something went wrong' : 'Cannot reach the server. Is the backend running?')

export default api
