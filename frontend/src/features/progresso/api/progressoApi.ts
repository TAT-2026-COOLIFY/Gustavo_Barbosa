import { axiosClient } from '@/lib/axiosClient'
import type { RegistroProgressoResponseDTO } from '@/types/progresso'

export interface RegistrarProgressoInput {
  dataRegistro: string | null
  pesoKg: number
  percentualGordura: number | null
  circunferenciaCinturaCm: number | null
  circunferenciaQuadrilCm: number | null
  circunferenciaBracoCm: number | null
  circunferenciaCoxaCm: number | null
  circunferenciaPeitoCm: number | null
  observacoes: string | null
}

export async function listarProgresso() {
  const { data } = await axiosClient.get<RegistroProgressoResponseDTO[]>('/progresso')
  return data
}

export async function getUltimoProgresso() {
  const { data } = await axiosClient.get<RegistroProgressoResponseDTO>('/progresso/ultimo')
  return data
}

export async function registrarProgresso(input: RegistrarProgressoInput) {
  const { data } = await axiosClient.post<RegistroProgressoResponseDTO>('/progresso', input)
  return data
}

export async function atualizarProgresso(id: number, input: RegistrarProgressoInput) {
  const { data } = await axiosClient.put<RegistroProgressoResponseDTO>(`/progresso/${id}`, input)
  return data
}

export async function removerProgresso(id: number) {
  await axiosClient.delete(`/progresso/${id}`)
}

export async function atualizarFotoProgresso(id: number, arquivo: File) {
  const formData = new FormData()
  formData.append('arquivo', arquivo)
  const { data } = await axiosClient.post<{ fotoUrl: string }>(`/progresso/${id}/foto`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}
