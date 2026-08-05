import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Dumbbell } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { descartarRascunho } from '@/features/ia/api/iaApi'
import type { RascunhoResponseDTO, RascunhoTreinoDados } from '@/types/ia'
import type { ExercicioResponseDTO } from '@/types/treino'

interface SugestaoTreinoCardProps {
  rascunho: RascunhoResponseDTO
  exercicios: ExercicioResponseDTO[]
  onDescartado: (rascunhoId: number) => void
}

export function SugestaoTreinoCard({ rascunho, exercicios, onDescartado }: SugestaoTreinoCardProps) {
  const navigate = useNavigate()
  const [descartando, setDescartando] = useState(false)
  const dados = rascunho.dados as RascunhoTreinoDados

  function nomeExercicio(id: number) {
    return exercicios.find((ex) => ex.id === id)?.nome ?? `Exercício #${id}`
  }

  async function onDescartar() {
    setDescartando(true)
    try {
      await descartarRascunho(rascunho.id)
      onDescartado(rascunho.id)
    } finally {
      setDescartando(false)
    }
  }

  function onEditar() {
    navigate('/treinos/novo', { state: { rascunhoIaId: rascunho.id, dados } })
  }

  if (rascunho.status !== 'PENDENTE') {
    return (
      <Card className="glass mt-2 max-w-[80%] border-0">
        <CardContent className="py-3 text-sm text-muted-foreground">
          Sugestão {rascunho.status === 'CONFIRMADO' ? 'salva como treino.' : 'descartada.'}
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="glass mt-2 max-w-[80%] border-0">
      <CardHeader className="pb-2">
        <CardTitle className="flex items-center gap-2 text-sm">
          <Dumbbell className="size-4 text-primary" />
          Sugestão de treino: {dados.nome}
        </CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-3">
        <ul className="flex flex-col gap-1 text-sm text-muted-foreground">
          {dados.exercicios.map((ex, index) => (
            <li key={index}>
              {nomeExercicio(ex.exercicioId)} — {ex.seriesAlvo}x{ex.repeticoesAlvo}
              {ex.cargaAlvoKg ? ` @ ${ex.cargaAlvoKg}kg` : ''}
            </li>
          ))}
        </ul>
        <div className="flex gap-2">
          <Button size="sm" onClick={onEditar}>
            Editar e salvar
          </Button>
          <Button size="sm" variant="outline" disabled={descartando} onClick={onDescartar}>
            Descartar
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}
