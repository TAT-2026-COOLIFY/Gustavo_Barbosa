import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, ChevronLeft, ChevronRight } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Checkbox } from '@/components/ui/checkbox'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { MacroProgressBar } from '@/features/dieta/components/MacroProgressBar'
import { obterDiario, registrarConsumo, removerConsumo } from '@/features/dieta/api/diarioApi'
import type { DiarioDiaResponseDTO } from '@/types/dieta'

function hoje() {
  return new Date().toISOString().slice(0, 10)
}

function somarDias(data: string, delta: number) {
  const d = new Date(`${data}T00:00:00`)
  d.setDate(d.getDate() + delta)
  return d.toISOString().slice(0, 10)
}

export function DiarioPage() {
  const [data, setData] = useState(hoje())
  const [dia, setDia] = useState<DiarioDiaResponseDTO | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [semDietaAtiva, setSemDietaAtiva] = useState(false)

  async function carregar() {
    setCarregando(true)
    try {
      const dado = await obterDiario(data)
      setDia(dado)
      setSemDietaAtiva(false)
    } catch {
      setDia(null)
      setSemDietaAtiva(true)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    carregar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [data])

  async function handleToggleConsumida(refeicaoId: number, consumida: boolean) {
    if (consumida) {
      await removerConsumo(refeicaoId, data)
    } else {
      await registrarConsumo(refeicaoId, data)
    }
    carregar()
  }

  return (
    <div className="relative min-h-svh bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex max-w-2xl flex-col gap-6">
        <div className="flex items-center justify-between">
          <Button asChild variant="glass" size="icon">
            <Link to="/dietas" aria-label="Voltar">
              <ArrowLeft />
            </Link>
          </Button>
          <ThemeToggle />
        </div>

        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="flex flex-col gap-4"
        >
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-semibold">Diário alimentar</h1>
            <div className="flex items-center gap-1">
              <Button variant="glass" size="icon" onClick={() => setData((d) => somarDias(d, -1))} aria-label="Dia anterior">
                <ChevronLeft />
              </Button>
              <span className="min-w-24 text-center text-sm text-muted-foreground">
                {new Date(`${data}T00:00:00`).toLocaleDateString('pt-BR')}
              </span>
              <Button variant="glass" size="icon" onClick={() => setData((d) => somarDias(d, 1))} aria-label="Próximo dia">
                <ChevronRight />
              </Button>
            </div>
          </div>

          {!carregando && semDietaAtiva && (
            <Card className="glass border-0">
              <CardContent className="flex flex-col items-center gap-3 py-8 text-center">
                <p className="text-sm text-muted-foreground">
                  Você ainda não tem uma dieta ativa. Ative uma dieta para acompanhar seu diário alimentar.
                </p>
                <Button asChild size="sm">
                  <Link to="/dietas">Ver minhas dietas</Link>
                </Button>
              </CardContent>
            </Card>
          )}

          {!carregando && dia && (
            <>
              <Card className="glass border-0">
                <CardHeader>
                  <CardTitle className="text-base">{dia.dietaNome}</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-4">
                  <MacroProgressBar
                    label="Calorias"
                    consumido={dia.caloriasConsumidas}
                    meta={dia.metaCaloriasKcal}
                    unidade="kcal"
                  />
                  <MacroProgressBar label="Proteína" consumido={dia.proteinaConsumidaG} meta={dia.metaProteinaG} unidade="g" />
                  <MacroProgressBar
                    label="Carboidrato"
                    consumido={dia.carboidratoConsumidoG}
                    meta={dia.metaCarboidratoG}
                    unidade="g"
                  />
                  <MacroProgressBar label="Gordura" consumido={dia.gorduraConsumidaG} meta={dia.metaGorduraG} unidade="g" />
                </CardContent>
              </Card>

              <div className="flex flex-col gap-2">
                {dia.refeicoes.map((refeicao) => (
                  <label
                    key={refeicao.refeicaoId}
                    className="glass flex cursor-pointer items-center gap-3 rounded-xl p-4 transition-opacity"
                  >
                    <Checkbox
                      checked={refeicao.consumida}
                      onCheckedChange={() => handleToggleConsumida(refeicao.refeicaoId, refeicao.consumida)}
                    />
                    <div className="flex-1">
                      <p className={refeicao.consumida ? 'text-sm font-medium line-through opacity-60' : 'text-sm font-medium'}>
                        {refeicao.nome}
                        {refeicao.horarioSugerido && (
                          <span className="ml-2 text-xs text-muted-foreground">{refeicao.horarioSugerido.slice(0, 5)}</span>
                        )}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {refeicao.calorias} kcal · {refeicao.proteinaG}g P · {refeicao.carboidratoG}g C · {refeicao.gorduraG}g G
                      </p>
                    </div>
                  </label>
                ))}
              </div>
            </>
          )}
        </motion.div>
      </div>
    </div>
  )
}
