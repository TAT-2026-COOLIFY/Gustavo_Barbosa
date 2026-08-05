import { Apple, Droplets, Dumbbell, Trophy, X } from 'lucide-react'

import { Card, CardContent } from '@/components/ui/card'
import type { NotificacaoResponseDTO, TipoNotificacao } from '@/types/notificacao'

const ICONES: Record<TipoNotificacao, typeof Droplets> = {
  AGUA: Droplets,
  TREINO: Dumbbell,
  REFEICAO: Apple,
  META: Trophy,
}

interface NotificacaoItemProps {
  notificacao: NotificacaoResponseDTO
  onMarcarLida: (id: number) => void
  onRemover: (id: number) => void
}

export function NotificacaoItem({ notificacao, onMarcarLida, onRemover }: NotificacaoItemProps) {
  const Icone = ICONES[notificacao.tipo]

  return (
    <Card className={`glass border-0 ${notificacao.lida ? 'opacity-60' : ''}`}>
      <CardContent className="flex items-start gap-3 py-4">
        <Icone className="mt-0.5 size-5 shrink-0 text-primary" />
        <button
          type="button"
          className="flex-1 text-left"
          onClick={() => !notificacao.lida && onMarcarLida(notificacao.id)}
        >
          <div className="flex items-center gap-2">
            <p className="font-medium">{notificacao.titulo}</p>
            {!notificacao.lida && <span className="size-2 shrink-0 rounded-full bg-primary" />}
          </div>
          <p className="text-sm text-muted-foreground">{notificacao.mensagem}</p>
          <p className="mt-1 text-xs text-muted-foreground">
            {new Date(notificacao.criadoEm).toLocaleString('pt-BR')}
          </p>
        </button>
        <button
          type="button"
          aria-label="Remover notificação"
          className="text-muted-foreground transition-colors hover:text-destructive"
          onClick={() => onRemover(notificacao.id)}
        >
          <X className="size-4" />
        </button>
      </CardContent>
    </Card>
  )
}
