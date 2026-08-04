import { Apple, CheckCircle2, Copy, Pencil, Star, Trash2 } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import type { DietaResumoDTO } from '@/types/dieta'

interface DietaCardProps {
  dieta: DietaResumoDTO
  own: boolean
  onAtivar: () => void
  onDuplicar: () => void
  onEditar?: () => void
  onExcluir?: () => void
  onToggleFavorito: () => void
}

export function DietaCard({ dieta, own, onAtivar, onDuplicar, onEditar, onExcluir, onToggleFavorito }: DietaCardProps) {
  return (
    <Card className="glass border-0">
      <CardHeader>
        <div className="flex items-start justify-between gap-2">
          <CardTitle className="flex items-center gap-2 text-base">
            <Apple className="size-4 shrink-0" />
            {dieta.nome}
            {dieta.ativa && <CheckCircle2 className="size-4 text-primary" aria-label="Dieta ativa" />}
          </CardTitle>
          <button
            type="button"
            onClick={onToggleFavorito}
            aria-label={dieta.favorito ? 'Remover dos favoritos' : 'Adicionar aos favoritos'}
            className="text-muted-foreground transition-colors hover:text-primary"
          >
            <Star className={dieta.favorito ? 'size-4 fill-primary text-primary' : 'size-4'} />
          </button>
        </div>
        <div className="flex flex-wrap gap-1.5">
          <Badge variant="secondary" className="w-fit">
            {dieta.metaCaloriasKcal} kcal
          </Badge>
          <Badge variant="secondary" className="w-fit">
            {dieta.quantidadeRefeicoes} refeição{dieta.quantidadeRefeicoes === 1 ? '' : 'ões'}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="flex flex-wrap gap-2">
        {!dieta.ativa && (
          <Button size="sm" onClick={onAtivar}>
            <CheckCircle2 />
            Ativar
          </Button>
        )}
        <Button size="sm" variant="outline" onClick={onDuplicar}>
          <Copy />
          Duplicar
        </Button>
        {own && onEditar && (
          <Button size="sm" variant="outline" onClick={onEditar}>
            <Pencil />
            Editar
          </Button>
        )}
        {own && onExcluir && (
          <Button size="sm" variant="outline" onClick={onExcluir}>
            <Trash2 />
            Excluir
          </Button>
        )}
      </CardContent>
    </Card>
  )
}
