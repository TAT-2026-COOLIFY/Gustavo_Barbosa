import { Copy, Dumbbell, Pencil, Play, Star, Trash2 } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import type { TreinoResumoDTO } from '@/types/treino'

interface TreinoCardProps {
  treino: TreinoResumoDTO
  own: boolean
  onIniciar: () => void
  onDuplicar: () => void
  onEditar?: () => void
  onExcluir?: () => void
  onToggleFavorito: () => void
}

export function TreinoCard({ treino, own, onIniciar, onDuplicar, onEditar, onExcluir, onToggleFavorito }: TreinoCardProps) {
  return (
    <Card className="glass border-0">
      <CardHeader>
        <div className="flex items-start justify-between gap-2">
          <CardTitle className="flex items-center gap-2 text-base">
            <Dumbbell className="size-4 shrink-0" />
            {treino.nome}
          </CardTitle>
          <button
            type="button"
            onClick={onToggleFavorito}
            aria-label={treino.favorito ? 'Remover dos favoritos' : 'Adicionar aos favoritos'}
            className="text-muted-foreground transition-colors hover:text-primary"
          >
            <Star className={treino.favorito ? 'size-4 fill-primary text-primary' : 'size-4'} />
          </button>
        </div>
        <Badge variant="secondary" className="w-fit">
          {treino.quantidadeExercicios} exercício{treino.quantidadeExercicios === 1 ? '' : 's'}
        </Badge>
      </CardHeader>
      <CardContent className="flex flex-wrap gap-2">
        <Button size="sm" onClick={onIniciar}>
          <Play />
          Iniciar
        </Button>
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
