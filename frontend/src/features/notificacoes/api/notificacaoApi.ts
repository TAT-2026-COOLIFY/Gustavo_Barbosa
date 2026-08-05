import { axiosClient } from '@/lib/axiosClient'
import type { NotificacaoResponseDTO } from '@/types/notificacao'

export async function listarNotificacoes() {
  const { data } = await axiosClient.get<NotificacaoResponseDTO[]>('/notificacoes')
  return data
}

export async function contarNotificacoesNaoLidas() {
  const { data } = await axiosClient.get<{ total: number }>('/notificacoes/nao-lidas')
  return data.total
}

export async function marcarNotificacaoLida(id: number) {
  const { data } = await axiosClient.put<NotificacaoResponseDTO>(`/notificacoes/${id}/lida`)
  return data
}

export async function marcarTodasNotificacoesLidas() {
  await axiosClient.put('/notificacoes/lidas')
}

export async function removerNotificacao(id: number) {
  await axiosClient.delete(`/notificacoes/${id}`)
}
