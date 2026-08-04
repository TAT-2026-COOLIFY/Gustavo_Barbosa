import type { TipoObjetivo } from '@/types/perfil'

export type CategoriaAlimento =
  | 'CEREAIS'
  | 'PROTEINAS'
  | 'LATICINIOS'
  | 'FRUTAS'
  | 'VEGETAIS'
  | 'GORDURAS'
  | 'BEBIDAS'
  | 'DOCES'
  | 'OUTROS'

export type NivelAtividade = 'SEDENTARIO' | 'LEVE' | 'MODERADO' | 'INTENSO' | 'MUITO_INTENSO'
export type OrigemDieta = 'MANUAL' | 'TEMPLATE' | 'IA'

export interface AlimentoResponseDTO {
  id: number
  nome: string
  marca: string | null
  categoria: CategoriaAlimento
  caloriasPor100g: number
  proteinaPor100g: number
  carboidratoPor100g: number
  gorduraPor100g: number
  fibraPor100g: number | null
  personalizado: boolean
}

export interface RefeicaoAlimentoResponseDTO {
  id: number
  alimentoId: number
  alimentoNome: string
  quantidadeGramas: number
  calorias: number
  proteinaG: number
  carboidratoG: number
  gorduraG: number
}

export interface RefeicaoResponseDTO {
  id: number
  nome: string
  ordem: number
  horarioSugerido: string | null
  caloriasTotal: number
  proteinaTotalG: number
  carboidratoTotalG: number
  gorduraTotalG: number
  alimentos: RefeicaoAlimentoResponseDTO[]
}

export interface DietaResponseDTO {
  id: number
  nome: string
  origem: OrigemDieta
  ativa: boolean
  favorito: boolean
  pesoReferenciaKg: number | null
  nivelAtividade: NivelAtividade | null
  tipoObjetivoReferencia: TipoObjetivo | null
  calculoAutomatico: boolean
  metaCaloriasKcal: number
  metaProteinaG: number
  metaCarboidratoG: number
  metaGorduraG: number
  metaAguaMl: number
  refeicoes: RefeicaoResponseDTO[]
}

export interface DietaResumoDTO {
  id: number
  nome: string
  origem: OrigemDieta
  ativa: boolean
  favorito: boolean
  metaCaloriasKcal: number
  metaProteinaG: number
  metaCarboidratoG: number
  metaGorduraG: number
  quantidadeRefeicoes: number
}

export interface MetaNutricionalCalculadaDTO {
  tmbKcal: number
  gastoTotalDiarioKcal: number
  metaCaloriasKcal: number
  metaProteinaG: number
  metaCarboidratoG: number
  metaGorduraG: number
  metaAguaMl: number
}

export interface RefeicaoAlimentoInput {
  alimentoId: number
  ordem: number
  quantidadeGramas: number
}

export interface RefeicaoInput {
  nome: string
  ordem: number
  horarioSugerido: string | null
  alimentos: RefeicaoAlimentoInput[]
}

export interface DiarioRefeicaoDTO {
  refeicaoId: number
  nome: string
  horarioSugerido: string | null
  calorias: number
  proteinaG: number
  carboidratoG: number
  gorduraG: number
  consumida: boolean
  consumidoEm: string | null
}

export interface DiarioDiaResponseDTO {
  data: string
  dietaId: number
  dietaNome: string
  metaCaloriasKcal: number
  metaProteinaG: number
  metaCarboidratoG: number
  metaGorduraG: number
  caloriasConsumidas: number
  proteinaConsumidaG: number
  carboidratoConsumidoG: number
  gorduraConsumidaG: number
  refeicoes: DiarioRefeicaoDTO[]
}

export interface DiarioHistoricoDiaDTO {
  data: string
  metaCaloriasKcal: number | null
  caloriasConsumidas: number
}
