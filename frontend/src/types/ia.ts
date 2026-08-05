import type { DietaInput } from '@/features/dieta/api/dietaApi'
import type { TreinoExercicioInput } from '@/types/treino'

export type PapelMensagemIa = 'USUARIO' | 'ASSISTENTE'
export type TipoRascunhoIa = 'TREINO' | 'DIETA'
export type StatusRascunhoIa = 'PENDENTE' | 'CONFIRMADO' | 'DESCARTADO'

export interface RascunhoTreinoDados {
  nome: string
  exercicios: TreinoExercicioInput[]
}

export type RascunhoDietaDados = DietaInput

export interface RascunhoResponseDTO {
  id: number
  tipo: TipoRascunhoIa
  status: StatusRascunhoIa
  dados: RascunhoTreinoDados | RascunhoDietaDados | null
}

export interface MensagemResponseDTO {
  id: number
  papel: PapelMensagemIa
  conteudo: string
  criadoEm: string
}

export interface MensagemComRascunhoDTO extends MensagemResponseDTO {
  rascunho: RascunhoResponseDTO | null
}

export interface EnviarMensagemResponseDTO {
  conversaId: number
  mensagemUsuario: MensagemResponseDTO
  mensagemAssistente: MensagemResponseDTO
  rascunho: RascunhoResponseDTO | null
}

export interface ConversaResumoDTO {
  id: number
  titulo: string | null
  criadoEm: string
  atualizadoEm: string
}

export interface ConversaDetalheDTO {
  id: number
  titulo: string | null
  mensagens: MensagemComRascunhoDTO[]
}
