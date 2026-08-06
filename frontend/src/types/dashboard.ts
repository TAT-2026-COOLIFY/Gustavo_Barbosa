import type { ObjetivoResponseDTO } from '@/types/perfil'

export interface ResumoImcDTO {
  pesoKg: number
  imc: number | null
  massaMagraKg: number | null
  dataRegistro: string
}

export interface ResumoAguaDashboardDTO {
  metaMl: number
  totalMl: number
  percentual: number
  diasConsecutivos: number
}

export interface ResumoTreinoDashboardDTO {
  treinouHoje: boolean
  volumeSemanalKg: number
}

export interface ResumoDietaDashboardDTO {
  temDietaAtiva: boolean
  dietaNome: string | null
  metaCaloriasKcal: number | null
  caloriasConsumidas: number | null
  metaProteinaG: number | null
  proteinaConsumidaG: number | null
  metaCarboidratoG: number | null
  carboidratoConsumidoG: number | null
  metaGorduraG: number | null
  gorduraConsumidaG: number | null
}

export interface ResumoMeditacaoDashboardDTO {
  diasConsecutivos: number
}

export interface ConquistaDTO {
  titulo: string
  descricao: string
}

export interface DashboardResponseDTO {
  imc: ResumoImcDTO | null
  objetivoAtivo: ObjetivoResponseDTO | null
  agua: ResumoAguaDashboardDTO
  treino: ResumoTreinoDashboardDTO
  dieta: ResumoDietaDashboardDTO
  meditacao: ResumoMeditacaoDashboardDTO
  notificacoesNaoLidas: number
  rascunhoIaPendente: boolean
  conquistas: ConquistaDTO[]
}
