import { axiosClient } from '@/lib/axiosClient'
import type {
  DietaResponseDTO,
  DietaResumoDTO,
  MetaNutricionalCalculadaDTO,
  NivelAtividade,
  RefeicaoInput,
} from '@/types/dieta'
import type { TipoObjetivo } from '@/types/perfil'

export interface DietaInput {
  nome: string
  pesoReferenciaKg: number | null
  nivelAtividade: NivelAtividade | null
  tipoObjetivoReferencia: TipoObjetivo | null
  calculoAutomatico: boolean
  metaCaloriasKcal: number | null
  metaProteinaG: number | null
  metaCarboidratoG: number | null
  metaGorduraG: number | null
  metaAguaMl: number | null
  refeicoes: RefeicaoInput[]
}

export async function listarMinhasDietas() {
  const { data } = await axiosClient.get<DietaResumoDTO[]>('/dietas')
  return data
}

export async function listarTemplates() {
  const { data } = await axiosClient.get<DietaResumoDTO[]>('/dietas/templates')
  return data
}

export async function obterDietaAtiva() {
  const { data } = await axiosClient.get<DietaResponseDTO>('/dietas/ativa')
  return data
}

export async function obterDieta(id: number) {
  const { data } = await axiosClient.get<DietaResponseDTO>(`/dietas/${id}`)
  return data
}

export async function criarDieta(input: DietaInput) {
  const { data } = await axiosClient.post<DietaResponseDTO>('/dietas', input)
  return data
}

export async function atualizarDieta(id: number, input: DietaInput) {
  const { data } = await axiosClient.put<DietaResponseDTO>(`/dietas/${id}`, input)
  return data
}

export async function excluirDieta(id: number) {
  await axiosClient.delete(`/dietas/${id}`)
}

export async function duplicarDieta(id: number) {
  const { data } = await axiosClient.post<DietaResponseDTO>(`/dietas/${id}/duplicar`)
  return data
}

export async function alternarFavoritoDieta(id: number) {
  const { data } = await axiosClient.post<{ favorito: boolean }>(`/dietas/${id}/favorito`)
  return data
}

export async function ativarDieta(id: number) {
  const { data } = await axiosClient.post<DietaResponseDTO>(`/dietas/${id}/ativar`)
  return data
}

export async function calcularMetas(input: {
  pesoReferenciaKg: number
  nivelAtividade: NivelAtividade
  tipoObjetivo: TipoObjetivo
}) {
  const { data } = await axiosClient.post<MetaNutricionalCalculadaDTO>('/dietas/calcular-metas', input)
  return data
}
