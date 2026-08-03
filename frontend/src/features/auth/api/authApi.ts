import { axiosClient } from '@/lib/axiosClient'
import type { LoginResponseDTO } from '@/types/auth'

export async function cadastrar(input: { nome: string; email: string; senha: string }) {
  const { data } = await axiosClient.post<LoginResponseDTO>('/auth/cadastro', input)
  return data
}

export async function login(input: { email: string; senha: string }) {
  const { data } = await axiosClient.post<LoginResponseDTO>('/auth/login', input)
  return data
}

export async function logout(refreshToken: string) {
  await axiosClient.post('/auth/logout', { refreshToken })
}

export async function esqueciSenha(email: string) {
  await axiosClient.post('/auth/esqueci-senha', { email })
}

export async function redefinirSenha(input: { token: string; novaSenha: string }) {
  await axiosClient.post('/auth/redefinir-senha', input)
}

export async function alterarSenha(input: { senhaAtual: string; novaSenha: string }) {
  await axiosClient.put('/auth/alterar-senha', input)
}
