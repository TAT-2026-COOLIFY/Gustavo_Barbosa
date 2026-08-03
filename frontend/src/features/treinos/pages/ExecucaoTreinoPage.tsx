import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Check, Flag } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { RestTimer } from '@/features/treinos/components/RestTimer'
import { formatarTempo, useElapsedSeconds } from '@/features/treinos/hooks/useElapsedSeconds'
import { concluirSessao, obterSessao, registrarSerie } from '@/features/treinos/api/treinoApi'
import type { SessaoTreinoResponseDTO } from '@/types/treino'

interface RegistroSerie {
  reps: string
  carga: string
  logado: boolean
}

export function ExecucaoTreinoPage() {
  const { sessaoId } = useParams()
  const navigate = useNavigate()
  const [sessao, setSessao] = useState<SessaoTreinoResponseDTO | null>(null)
  const [registros, setRegistros] = useState<Record<number, RegistroSerie[]>>({})
  const [descansoAtivo, setDescansoAtivo] = useState<{ treinoExercicioId: number; segundos: number } | null>(null)
  const [concluindo, setConcluindo] = useState(false)

  const elapsed = useElapsedSeconds(sessao?.dataExecucao ?? null)

  useEffect(() => {
    obterSessao(Number(sessaoId)).then((data) => {
      setSessao(data)
      const inicial: Record<number, RegistroSerie[]> = {}
      data.exercicios.forEach((te) => {
        inicial[te.id] = Array.from({ length: te.seriesAlvo }, () => ({
          reps: String(te.repeticoesAlvo),
          carga: te.cargaAlvoKg ? String(te.cargaAlvoKg) : '',
          logado: false,
        }))
      })
      setRegistros(inicial)
    })
  }, [sessaoId])

  function atualizarCampo(treinoExercicioId: number, indice: number, campo: 'reps' | 'carga', valor: string) {
    setRegistros((atual) => ({
      ...atual,
      [treinoExercicioId]: atual[treinoExercicioId].map((r, i) => (i === indice ? { ...r, [campo]: valor } : r)),
    }))
  }

  async function handleRegistrarSerie(treinoExercicioId: number, indice: number, descansoSegundos: number | null) {
    const registro = registros[treinoExercicioId][indice]
    await registrarSerie(Number(sessaoId), {
      treinoExercicioId,
      numeroSerie: indice + 1,
      repeticoesRealizadas: Number(registro.reps),
      cargaRealizadaKg: registro.carga ? Number(registro.carga) : null,
    })

    setRegistros((atual) => ({
      ...atual,
      [treinoExercicioId]: atual[treinoExercicioId].map((r, i) => (i === indice ? { ...r, logado: true } : r)),
    }))

    if (descansoSegundos && descansoSegundos > 0) {
      setDescansoAtivo({ treinoExercicioId, segundos: descansoSegundos })
    }
  }

  async function handleConcluir() {
    setConcluindo(true)
    await concluirSessao(Number(sessaoId), elapsed)
    navigate('/treinos/historico')
  }

  if (!sessao) {
    return (
      <div className="flex min-h-svh items-center justify-center bg-background">
        <div className="size-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <div className="relative min-h-svh bg-background px-6 py-10 pb-28">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex max-w-2xl flex-col gap-6">
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass flex flex-col items-center gap-1 rounded-2xl px-6 py-5 text-center shadow-xl"
        >
          <p className="text-sm text-muted-foreground">{sessao.treinoNome}</p>
          <p className="font-mono text-4xl font-semibold tabular-nums">{formatarTempo(elapsed)}</p>
        </motion.div>

        <div className="flex flex-col gap-4">
          {sessao.exercicios.map((te) => (
            <Card key={te.id} className="glass border-0">
              <CardHeader>
                <CardTitle className="flex items-center justify-between text-base">
                  <span>{te.exercicio.nome}</span>
                  <Badge variant="secondary">
                    {te.seriesAlvo}x{te.repeticoesAlvo}
                  </Badge>
                </CardTitle>
              </CardHeader>
              <CardContent className="flex flex-col gap-2">
                {registros[te.id]?.map((registro, indice) => (
                  <div key={indice} className="flex items-center gap-2">
                    <span className="w-14 shrink-0 text-sm text-muted-foreground">Série {indice + 1}</span>
                    <Input
                      className="w-20"
                      placeholder="Reps"
                      value={registro.reps}
                      disabled={registro.logado}
                      onChange={(e) => atualizarCampo(te.id, indice, 'reps', e.target.value)}
                    />
                    <Input
                      className="w-24"
                      placeholder="Carga kg"
                      value={registro.carga}
                      disabled={registro.logado}
                      onChange={(e) => atualizarCampo(te.id, indice, 'carga', e.target.value)}
                    />
                    <Button
                      size="sm"
                      variant={registro.logado ? 'secondary' : 'default'}
                      disabled={registro.logado}
                      onClick={() => handleRegistrarSerie(te.id, indice, te.descansoSegundos)}
                    >
                      {registro.logado ? <Check /> : 'Registrar'}
                    </Button>
                  </div>
                ))}
                {descansoAtivo?.treinoExercicioId === te.id && (
                  <RestTimer segundos={descansoAtivo.segundos} onFim={() => setDescansoAtivo(null)} />
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      <div className="fixed inset-x-0 bottom-0 border-t bg-background/80 p-4 backdrop-blur-md">
        <div className="mx-auto flex max-w-2xl justify-end">
          <Button size="lg" onClick={handleConcluir} disabled={concluindo}>
            <Flag />
            {concluindo ? 'Concluindo...' : 'Concluir treino'}
          </Button>
        </div>
      </div>
    </div>
  )
}
