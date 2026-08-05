import { axiosClient } from '@/lib/axiosClient'
import type {
  HistoricoDiaHidratacaoDTO,
  MetaHidratacaoResponseDTO,
  ResumoDiaHidratacaoResponseDTO,
  SequenciaHidratacaoResponseDTO,
} from '@/types/hidratacao'

export async function getMeta() {
  const { data } = await axiosClient.get<MetaHidratacaoResponseDTO>('/hidratacao/meta')
  return data
}

export async function atualizarMeta(metaMl: number) {
  const { data } = await axiosClient.put<MetaHidratacaoResponseDTO>('/hidratacao/meta', { metaMl })
  return data
}

export async function getResumoHoje() {
  const { data } = await axiosClient.get<ResumoDiaHidratacaoResponseDTO>('/hidratacao/hoje')
  return data
}

export async function registrarConsumo(quantidadeMl: number) {
  const { data } = await axiosClient.post<ResumoDiaHidratacaoResponseDTO>('/hidratacao/registros', { quantidadeMl })
  return data
}

export async function removerRegistro(id: number) {
  await axiosClient.delete(`/hidratacao/registros/${id}`)
}

export async function getHistorico(dias = 7) {
  const { data } = await axiosClient.get<HistoricoDiaHidratacaoDTO[]>('/hidratacao/historico', { params: { dias } })
  return data
}

export async function getSequencia() {
  const { data } = await axiosClient.get<SequenciaHidratacaoResponseDTO>('/hidratacao/sequencia')
  return data
}
