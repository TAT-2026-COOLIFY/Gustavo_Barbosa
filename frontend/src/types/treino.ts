export type GrupoMuscular = 'PEITO' | 'COSTAS' | 'PERNAS' | 'OMBROS' | 'BRACOS' | 'ABDOMEN' | 'CARDIO' | 'CORPO_INTEIRO'
export type OrigemTreino = 'MANUAL' | 'TEMPLATE' | 'IA'

export interface ExercicioResponseDTO {
  id: number
  nome: string
  grupoMuscular: GrupoMuscular
  descricao: string | null
  equipamento: string | null
}

export interface TreinoExercicioResponseDTO {
  id: number
  exercicio: ExercicioResponseDTO
  ordem: number
  seriesAlvo: number
  repeticoesAlvo: number
  cargaAlvoKg: number | null
  descansoSegundos: number | null
}

export interface TreinoExercicioInput {
  exercicioId: number
  ordem: number
  seriesAlvo: number
  repeticoesAlvo: number
  cargaAlvoKg: number | null
  descansoSegundos: number | null
}

export interface TreinoResponseDTO {
  id: number
  nome: string
  origem: OrigemTreino
  favorito: boolean
  exercicios: TreinoExercicioResponseDTO[]
}

export interface TreinoResumoDTO {
  id: number
  nome: string
  origem: OrigemTreino
  favorito: boolean
  quantidadeExercicios: number
}

export interface SessaoTreinoResponseDTO {
  id: number
  treinoId: number
  treinoNome: string
  dataExecucao: string
  duracaoSegundos: number | null
  concluido: boolean
  exercicios: TreinoExercicioResponseDTO[]
}

export interface SerieExecutadaResponseDTO {
  id: number
  treinoExercicioId: number
  numeroSerie: number
  repeticoesRealizadas: number
  cargaRealizadaKg: number | null
}

export interface HistoricoSessaoResponseDTO {
  id: number
  treinoNome: string
  dataExecucao: string
  duracaoSegundos: number | null
  concluido: boolean
  totalSeries: number
}
