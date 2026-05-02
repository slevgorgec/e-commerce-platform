import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

// Attach access token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Refresh token on 401 — sadece daha önce login olmuş kullanıcılar için
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config

    if (error.response?.status === 401 && !original._retry) {
      original._retry = true
      const refreshToken = localStorage.getItem('refreshToken')
      const accessToken = localStorage.getItem('accessToken')

      // Token hiç yoksa (anonim kullanıcı) — sadece hatayı ilet, yönlendirme yapma
      if (!refreshToken || !accessToken) {
        return Promise.reject(error)
      }

      try {
        const { data } = await axios.post(`${BASE_URL}/api/auth/refresh`, {
          refreshToken,
        })
        const newAccessToken: string = data.data.accessToken
        localStorage.setItem('accessToken', newAccessToken)
        original.headers.Authorization = `Bearer ${newAccessToken}`
        return api(original)
      } catch {
        // Refresh başarısız → oturumu kapat
        localStorage.clear()
        window.location.href = '/login'
        return Promise.reject(error)
      }
    }

    return Promise.reject(error)
  }
)
