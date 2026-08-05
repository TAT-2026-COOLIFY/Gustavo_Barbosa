import { axiosClient } from '@/lib/axiosClient'
import type { DietaResponseDTO } from '@/types/dieta'
import type {
  ConversaDetalheDTO,
  ConversaResumoDTO,
  EnviarMensagemResponseDTO,
  RascunhoDietaDados,
  RascunhoTreinoDados,
} from '@/types/ia'
import type { TreinoResponseDTO } from '@/types/treino'

export async function enviarMensagem(mensagem: string, conversaId?: number) {
  const { data } = await axiosClient.post<EnviarMensagemResponseDTO>('/ia/mensagens', { conversaId: conversaId ?? null, mensagem })
  return data
}

export async function listarConversas() {
  const { data } = await axiosClient.get<ConversaResumoDTO[]>('/ia/conversas')
  return data
}

export async function obterConversa(id: number) {
  const { data } = await axiosClient.get<ConversaDetalheDTO>(`/ia/conversas/${id}`)
  return data
}

export async function confirmarRascunhoTreino(rascunhoId: number, dados: RascunhoTreinoDados) {
  const { data } = await axiosClient.post<TreinoResponseDTO>(`/ia/rascunhos/${rascunhoId}/confirmar-treino`, dados)
  return data
}

export async function confirmarRascunhoDieta(rascunhoId: number, dados: RascunhoDietaDados) {
  const { data } = await axiosClient.post<DietaResponseDTO>(`/ia/rascunhos/${rascunhoId}/confirmar-dieta`, dados)
  return data
}

export async function descartarRascunho(rascunhoId: number) {
  await axiosClient.delete(`/ia/rascunhos/${rascunhoId}`)
}
