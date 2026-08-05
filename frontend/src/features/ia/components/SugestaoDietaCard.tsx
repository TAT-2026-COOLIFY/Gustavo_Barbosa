import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Apple } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { descartarRascunho } from '@/features/ia/api/iaApi'
import type { AlimentoResponseDTO } from '@/types/dieta'
import type { RascunhoDietaDados, RascunhoResponseDTO } from '@/types/ia'

interface SugestaoDietaCardProps {
  rascunho: RascunhoResponseDTO
  alimentos: AlimentoResponseDTO[]
  onDescartado: (rascunhoId: number) => void
}

export function SugestaoDietaCard({ rascunho, alimentos, onDescartado }: SugestaoDietaCardProps) {
  const navigate = useNavigate()
  const [descartando, setDescartando] = useState(false)
  const dados = rascunho.dados as RascunhoDietaDados

  function nomeAlimento(id: number) {
    return alimentos.find((al) => al.id === id)?.nome ?? `Alimento #${id}`
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
    navigate('/dietas/novo', { state: { rascunhoIaId: rascunho.id, dados } })
  }

  if (rascunho.status !== 'PENDENTE') {
    return (
      <Card className="glass mt-2 max-w-[80%] border-0">
        <CardContent className="py-3 text-sm text-muted-foreground">
          Sugestão {rascunho.status === 'CONFIRMADO' ? 'salva como dieta.' : 'descartada.'}
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="glass mt-2 max-w-[80%] border-0">
      <CardHeader className="pb-2">
        <CardTitle className="flex items-center gap-2 text-sm">
          <Apple className="size-4 text-primary" />
          Sugestão de dieta: {dados.nome}
        </CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-3">
        <ul className="flex flex-col gap-2 text-sm text-muted-foreground">
          {dados.refeicoes.map((refeicao, index) => (
            <li key={index}>
              <p className="font-medium text-foreground">
                {refeicao.nome}
                {refeicao.horarioSugerido ? ` · ${refeicao.horarioSugerido}` : ''}
              </p>
              <p>{refeicao.alimentos.map((a) => `${nomeAlimento(a.alimentoId)} (${a.quantidadeGramas}g)`).join(', ')}</p>
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
