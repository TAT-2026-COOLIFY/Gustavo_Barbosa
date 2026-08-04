import { axiosClient } from '@/lib/axiosClient'
import type { DiarioDiaResponseDTO, DiarioHistoricoDiaDTO, DiarioRefeicaoDTO } from '@/types/dieta'

export async function obterDiario(data?: string) {
  const { data: dia } = await axiosClient.get<DiarioDiaResponseDTO>('/diario', { params: { data } })
  return dia
}

export async function registrarConsumo(refeicaoId: number, data?: string) {
  const { data: registro } = await axiosClient.post<DiarioRefeicaoDTO>(`/diario/refeicoes/${refeicaoId}`, null, {
    params: { data },
  })
  return registro
}

export async function removerConsumo(refeicaoId: number, data?: string) {
  await axiosClient.delete(`/diario/refeicoes/${refeicaoId}`, { params: { data } })
}

export async function historicoDiario(dias = 7) {
  const { data } = await axiosClient.get<DiarioHistoricoDiaDTO[]>('/diario/historico', { params: { dias } })
  return data
}
