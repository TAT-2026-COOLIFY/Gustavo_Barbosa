import { Trophy } from 'lucide-react'

import type { ConquistaDTO } from '@/types/dashboard'

export function ConquistaBadge({ conquista }: { conquista: ConquistaDTO }) {
  return (
    <div className="glass flex items-start gap-2.5 rounded-xl px-3.5 py-2.5">
      <Trophy className="mt-0.5 size-4 shrink-0 text-primary" />
      <div className="flex flex-col">
        <span className="text-sm font-medium">{conquista.titulo}</span>
        <span className="text-xs text-muted-foreground">{conquista.descricao}</span>
      </div>
    </div>
  )
}
