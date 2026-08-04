import { axiosClient } from '@/lib/axiosClient'
import type { AlimentoResponseDTO, CategoriaAlimento } from '@/types/dieta'

export async function buscarAlimentos(q?: string, categoria?: CategoriaAlimento) {
  const { data } = await axiosClient.get<AlimentoResponseDTO[]>('/alimentos', {
    params: { q: q || undefined, categoria },
  })
  return data
}

export async function criarAlimento(input: {
  nome: string
  marca: string | null
  categoria: CategoriaAlimento
  caloriasPor100g: number
  proteinaPor100g: number
  carboidratoPor100g: number
  gorduraPor100g: number
  fibraPor100g: number | null
}) {
  const { data } = await axiosClient.post<AlimentoResponseDTO>('/alimentos', input)
  return data
}
