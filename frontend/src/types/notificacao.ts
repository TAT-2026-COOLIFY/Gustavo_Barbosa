export type TipoNotificacao = 'AGUA' | 'TREINO' | 'REFEICAO' | 'META'

export interface NotificacaoResponseDTO {
  id: number
  tipo: TipoNotificacao
  titulo: string
  mensagem: string
  lida: boolean
  criadoEm: string
}
