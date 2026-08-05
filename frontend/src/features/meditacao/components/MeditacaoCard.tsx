import { Wind } from 'lucide-react'

import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import type { MeditacaoResponseDTO } from '@/types/meditacao'

const LABELS_TIPO: Record<string, string> = {
  RESPIRACAO: 'Respiração',
  RELAXAMENTO: 'Relaxamento',
  FOCO: 'Foco',
  SONO: 'Sono',
  ANSIEDADE: 'Ansiedade',
}

interface MeditacaoCardProps {
  meditacao: MeditacaoResponseDTO
  onIniciar: (id: number) => void
  iniciando: boolean
}

export function MeditacaoCard({ meditacao, onIniciar, iniciando }: MeditacaoCardProps) {
  return (
    <Card className="glass border-0">
      <CardContent className="flex items-center gap-3 py-4">
        {meditacao.tipo === 'RESPIRACAO' && <Wind className="size-5 shrink-0 text-primary" />}
        <div className="flex-1">
          <div className="flex items-center gap-2">
            <p className="font-medium">{meditacao.nome}</p>
            <Badge variant="glass">{LABELS_TIPO[meditacao.tipo]}</Badge>
          </div>
          <p className="text-sm text-muted-foreground">
            {meditacao.duracaoMinutos} min{meditacao.descricao ? ` · ${meditacao.descricao}` : ''}
          </p>
        </div>
        <Button size="sm" disabled={iniciando} onClick={() => onIniciar(meditacao.id)}>
          Iniciar
        </Button>
      </CardContent>
    </Card>
  )
}
