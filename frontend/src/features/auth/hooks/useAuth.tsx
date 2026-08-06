import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'

import * as authApi from '@/features/auth/api/authApi'
import { axiosClient, getRefreshToken, setAccessToken, setOnAuthFailure, setRefreshToken } from '@/lib/axiosClient'
import type { LoginResponseDTO, UsuarioResponseDTO } from '@/types/auth'

interface AuthContextValue {
  usuario: UsuarioResponseDTO | null
  status: 'loading' | 'authenticated' | 'unauthenticated'
  entrar: (email: string, senha: string) => Promise<void>
  cadastrar: (nome: string, email: string, senha: string) => Promise<void>
  sair: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioResponseDTO | null>(null)
  const [status, setStatus] = useState<AuthContextValue['status']>('loading')

  useEffect(() => {
    setOnAuthFailure(() => {
      setUsuario(null)
      setStatus('unauthenticated')
    })

    const refreshToken = getRefreshToken()
    if (!refreshToken) {
      setStatus('unauthenticated')
      return
    }

    restaurarSessao(refreshToken)

    return () => setOnAuthFailure(null)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function restaurarSessao(refreshToken: string) {
    try {
      const { data } = await axiosClient.post<LoginResponseDTO>('/auth/refresh', { refreshToken })
      setAccessToken(data.accessToken)
      setRefreshToken(data.refreshToken)
      setUsuario(data.usuario)
      setStatus('authenticated')
    } catch {
      setAccessToken(null)
      setRefreshToken(null)
      setStatus('unauthenticated')
    }
  }

  async function entrar(email: string, senha: string) {
    const data = await authApi.login({ email, senha })
    setAccessToken(data.accessToken)
    setRefreshToken(data.refreshToken)
    setUsuario(data.usuario)
    setStatus('authenticated')
  }

  async function cadastrar(nome: string, email: string, senha: string) {
    const data = await authApi.cadastrar({ nome, email, senha })
    setAccessToken(data.accessToken)
    setRefreshToken(data.refreshToken)
    setUsuario(data.usuario)
    setStatus('authenticated')
  }

  async function sair() {
    const refreshToken = getRefreshToken()
    setAccessToken(null)
    setRefreshToken(null)
    setUsuario(null)
    setStatus('unauthenticated')
    if (refreshToken) {
      await authApi.logout(refreshToken).catch(() => undefined)
    }
  }

  return (
    <AuthContext.Provider value={{ usuario, status, entrar, cadastrar, sair }}>{children}</AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider')
  }
  return context
}
