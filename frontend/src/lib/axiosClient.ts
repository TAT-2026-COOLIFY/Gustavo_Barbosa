import axios from 'axios'

import type { LoginResponseDTO } from '@/types/auth'

const REFRESH_TOKEN_STORAGE_KEY = 'ironfit.refreshToken'

export const axiosClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

let accessToken: string | null = null
let onAuthFailure: (() => void) | null = null
let refreshPromise: Promise<string | null> | null = null

// "Lembrar minha conta": localStorage sobrevive ao fechar o navegador, sessionStorage some junto com a aba.
// O storage em uso é determinado de forma preguiçosa a partir de onde o token já está guardado (para que
// renovações automáticas do interceptor, que não sabem a escolha do usuário, continuem escrevendo no mesmo
// lugar) — só é reatribuído explicitamente no momento do login, via o parâmetro `lembrar`.
let refreshTokenStorage: Storage | null = null

function storageAtual(): Storage {
  if (!refreshTokenStorage) {
    if (localStorage.getItem(REFRESH_TOKEN_STORAGE_KEY)) {
      refreshTokenStorage = localStorage
    } else if (sessionStorage.getItem(REFRESH_TOKEN_STORAGE_KEY)) {
      refreshTokenStorage = sessionStorage
    } else {
      refreshTokenStorage = localStorage
    }
  }
  return refreshTokenStorage
}

export function setAccessToken(token: string | null) {
  accessToken = token
}

export function getRefreshToken(): string | null {
  return storageAtual().getItem(REFRESH_TOKEN_STORAGE_KEY)
}

/**
 * @param lembrar Só deve ser informado no momento do login/cadastro (decide entre localStorage e
 * sessionStorage). Renovações automáticas do token (interceptor de 401, restauração de sessão) chamam
 * esta função sem esse parâmetro, preservando o storage já em uso.
 */
export function setRefreshToken(token: string | null, lembrar?: boolean) {
  if (lembrar !== undefined) {
    refreshTokenStorage = lembrar ? localStorage : sessionStorage
  }

  const storage = storageAtual()
  const outroStorage = storage === localStorage ? sessionStorage : localStorage
  outroStorage.removeItem(REFRESH_TOKEN_STORAGE_KEY)

  if (token) {
    storage.setItem(REFRESH_TOKEN_STORAGE_KEY, token)
  } else {
    storage.removeItem(REFRESH_TOKEN_STORAGE_KEY)
  }
}

export function setOnAuthFailure(callback: (() => void) | null) {
  onAuthFailure = callback
}

axiosClient.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }
  return config
})

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) return null

  if (!refreshPromise) {
    refreshPromise = axios
      .post<LoginResponseDTO>('/api/auth/refresh', { refreshToken })
      .then(({ data }) => {
        setAccessToken(data.accessToken)
        setRefreshToken(data.refreshToken)
        return data.accessToken
      })
      .catch(() => {
        setAccessToken(null)
        setRefreshToken(null)
        onAuthFailure?.()
        return null
      })
      .finally(() => {
        refreshPromise = null
      })
  }

  return refreshPromise
}

axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    const isAuthRoute = originalRequest?.url?.startsWith('/auth/')

    if (error.response?.status === 401 && !originalRequest._retry && !isAuthRoute) {
      originalRequest._retry = true
      const newAccessToken = await refreshAccessToken()

      if (newAccessToken) {
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
        return axiosClient(originalRequest)
      }
    }

    return Promise.reject(error)
  },
)
