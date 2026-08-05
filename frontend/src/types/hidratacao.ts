export interface MetaHidratacaoResponseDTO {
  id: number
  metaMl: number
}

export interface RegistroHidratacaoResponseDTO {
  id: number
  quantidadeMl: number
  registradoEm: string
}

export interface ResumoDiaHidratacaoResponseDTO {
  data: string
  metaMl: number
  totalMl: number
  percentual: number
  registros: RegistroHidratacaoResponseDTO[]
}

export interface HistoricoDiaHidratacaoDTO {
  data: string
  metaMl: number
  totalMl: number
  atingiu: boolean
}

export interface SequenciaHidratacaoResponseDTO {
  diasConsecutivos: number
}
