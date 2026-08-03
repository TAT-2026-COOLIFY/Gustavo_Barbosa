import axios from 'axios'

export const axiosClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

let accessToken: string | null = null

export function setAccessToken(token: string | null) {
  accessToken = token
}

axiosClient.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }
  return config
})

// A Fase 1 (Auth) adiciona aqui o interceptor de resposta que,
// ao receber 401, chama /api/auth/refresh e repete a requisição original.
