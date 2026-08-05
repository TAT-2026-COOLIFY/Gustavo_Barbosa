import { axiosClient } from '@/lib/axiosClient'
import type {
  HistoricoMeditacaoResponseDTO,
  MeditacaoResponseDTO,
  SequenciaMeditacaoResponseDTO,
  SessaoMeditacaoResponseDTO,
  TipoMeditacao,
} from '@/types/meditacao'

export async function listarMeditacoes(tipo?: TipoMeditacao) {
  const { data } = await axiosClient.get<MeditacaoResponseDTO[]>('/meditacoes', { params: { tipo } })
  return data
}

export async function obterMeditacao(id: number) {
  const { data } = await axiosClient.get<MeditacaoResponseDTO>(`/meditacoes/${id}`)
  return data
}

export async function iniciarMeditacao(id: number) {
  const { data } = await axiosClient.post<SessaoMeditacaoResponseDTO>(`/meditacoes/${id}/iniciar`)
  return data
}

export async function obterSessaoMeditacao(sessaoId: number) {
  const { data } = await axiosClient.get<SessaoMeditacaoResponseDTO>(`/sessoes-meditacao/${sessaoId}`)
  return data
}

export async function concluirSessaoMeditacao(sessaoId: number, duracaoSegundos: number) {
  const { data } = await axiosClient.put<SessaoMeditacaoResponseDTO>(`/sessoes-meditacao/${sessaoId}/concluir`, {
    duracaoSegundos,
  })
  return data
}

export async function historicoMeditacao() {
  const { data } = await axiosClient.get<HistoricoMeditacaoResponseDTO[]>('/meditacoes/historico')
  return data
}

export async function sequenciaMeditacao() {
  const { data } = await axiosClient.get<SequenciaMeditacaoResponseDTO>('/meditacoes/sequencia')
  return data
}
