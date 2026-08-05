export type TipoMeditacao = 'RESPIRACAO' | 'RELAXAMENTO' | 'FOCO' | 'SONO' | 'ANSIEDADE'

export interface MeditacaoResponseDTO {
  id: number
  nome: string
  tipo: TipoMeditacao
  duracaoMinutos: number
  descricao: string | null
  instrucoes: string | null
  inspirarSegundos: number | null
  segurarSegundos: number | null
  expirarSegundos: number | null
}

export interface SessaoMeditacaoResponseDTO {
  id: number
  meditacaoId: number
  meditacaoNome: string
  tipo: TipoMeditacao
  duracaoMinutosSugerida: number
  inspirarSegundos: number | null
  segurarSegundos: number | null
  expirarSegundos: number | null
  dataRealizacao: string
  duracaoSegundos: number | null
  concluido: boolean
}

export interface HistoricoMeditacaoResponseDTO {
  id: number
  meditacaoNome: string
  tipo: TipoMeditacao
  dataRealizacao: string
  duracaoSegundos: number | null
  concluido: boolean
}

export interface SequenciaMeditacaoResponseDTO {
  diasConsecutivos: number
}
