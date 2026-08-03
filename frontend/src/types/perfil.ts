export type Sexo = 'MASCULINO' | 'FEMININO' | 'OUTRO'
export type NivelExperiencia = 'INICIANTE' | 'INTERMEDIARIO' | 'AVANCADO'
export type TipoObjetivo = 'EMAGRECIMENTO' | 'HIPERTROFIA' | 'MANUTENCAO' | 'CONDICIONAMENTO' | 'SAUDE'

export interface PerfilResponseDTO {
  nome: string
  email: string
  fotoPerfilUrl: string | null
  dataNascimento: string | null
  sexo: Sexo | null
  alturaCm: number | null
  nivelExperiencia: NivelExperiencia | null
  bio: string | null
}

export interface ObjetivoResponseDTO {
  id: number
  tipo: TipoObjetivo
  pesoMetaKg: number | null
  dataInicio: string
  dataAlvo: string | null
  ativo: boolean
}
