import { axiosClient } from '@/lib/axiosClient'
import type {
  ExercicioResponseDTO,
  GrupoMuscular,
  HistoricoSessaoResponseDTO,
  SerieExecutadaResponseDTO,
  SessaoTreinoResponseDTO,
  TreinoExercicioInput,
  TreinoResponseDTO,
  TreinoResumoDTO,
} from '@/types/treino'

export async function listarMeusTreinos() {
  const { data } = await axiosClient.get<TreinoResumoDTO[]>('/treinos')
  return data
}

export async function listarTemplates() {
  const { data } = await axiosClient.get<TreinoResumoDTO[]>('/treinos/templates')
  return data
}

export async function obterTreino(id: number) {
  const { data } = await axiosClient.get<TreinoResponseDTO>(`/treinos/${id}`)
  return data
}

export async function criarTreino(input: { nome: string; exercicios: TreinoExercicioInput[] }) {
  const { data } = await axiosClient.post<TreinoResponseDTO>('/treinos', input)
  return data
}

export async function atualizarTreino(id: number, input: { nome: string; exercicios: TreinoExercicioInput[] }) {
  const { data } = await axiosClient.put<TreinoResponseDTO>(`/treinos/${id}`, input)
  return data
}

export async function excluirTreino(id: number) {
  await axiosClient.delete(`/treinos/${id}`)
}

export async function duplicarTreino(id: number) {
  const { data } = await axiosClient.post<TreinoResponseDTO>(`/treinos/${id}/duplicar`)
  return data
}

export async function alternarFavoritoTreino(id: number) {
  const { data } = await axiosClient.post<{ favorito: boolean }>(`/treinos/${id}/favorito`)
  return data
}

export async function listarExercicios(grupoMuscular?: GrupoMuscular) {
  const { data } = await axiosClient.get<ExercicioResponseDTO[]>('/exercicios', {
    params: grupoMuscular ? { grupoMuscular } : undefined,
  })
  return data
}

export async function iniciarTreino(id: number) {
  const { data } = await axiosClient.post<SessaoTreinoResponseDTO>(`/treinos/${id}/iniciar`)
  return data
}

export async function obterSessao(sessaoId: number) {
  const { data } = await axiosClient.get<SessaoTreinoResponseDTO>(`/sessoes-treino/${sessaoId}`)
  return data
}

export async function registrarSerie(
  sessaoId: number,
  input: { treinoExercicioId: number; numeroSerie: number; repeticoesRealizadas: number; cargaRealizadaKg: number | null },
) {
  const { data } = await axiosClient.post<SerieExecutadaResponseDTO>(`/sessoes-treino/${sessaoId}/series`, input)
  return data
}

export async function concluirSessao(sessaoId: number, duracaoSegundos: number) {
  const { data } = await axiosClient.put<SessaoTreinoResponseDTO>(`/sessoes-treino/${sessaoId}/concluir`, {
    duracaoSegundos,
  })
  return data
}

export async function historicoTreinos() {
  const { data } = await axiosClient.get<HistoricoSessaoResponseDTO[]>('/treinos/historico')
  return data
}

export async function volumeSemanal() {
  const { data } = await axiosClient.get<{ volumeKg: number }>('/treinos/volume-semanal')
  return data
}
