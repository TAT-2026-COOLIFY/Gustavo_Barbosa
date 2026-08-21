import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, Droplets, Flame } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { HistoricoAguaChart } from '@/features/hidratacao/components/HistoricoAguaChart'
import { RegistroAguaCard } from '@/features/hidratacao/components/RegistroAguaCard'
import {
  getHistorico,
  getResumoHoje,
  getSequencia,
  registrarConsumo,
  removerRegistro,
} from '@/features/hidratacao/api/hidratacaoApi'
import type { HistoricoDiaHidratacaoDTO, ResumoDiaHidratacaoResponseDTO } from '@/types/hidratacao'

export function HidratacaoPage() {
  const [resumo, setResumo] = useState<ResumoDiaHidratacaoResponseDTO | null>(null)
  const [historico, setHistorico] = useState<HistoricoDiaHidratacaoDTO[]>([])
  const [diasConsecutivos, setDiasConsecutivos] = useState(0)
  const [adicionando, setAdicionando] = useState(false)
  const [pulseKey, setPulseKey] = useState(0)

  const carregar = useCallback(async () => {
    const [resumoAtual, historicoAtual, sequenciaAtual] = await Promise.all([
      getResumoHoje(),
      getHistorico(7),
      getSequencia(),
    ])
    setResumo(resumoAtual)
    setHistorico(historicoAtual)
    setDiasConsecutivos(sequenciaAtual.diasConsecutivos)
  }, [])

  useEffect(() => {
    carregar()
  }, [carregar])

  async function onAdicionar(quantidadeMl: number) {
    setAdicionando(true)
    try {
      await registrarConsumo(quantidadeMl)
      await carregar()
      setPulseKey((chave) => chave + 1)
    } finally {
      setAdicionando(false)
    }
  }

  async function onRemover(registroId: number) {
    await removerRegistro(registroId)
    await carregar()
  }

  if (!resumo) {
    return (
      <div className="flex min-h-svh items-center justify-center bg-background">
        <div className="size-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <div className="relative min-h-svh bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex max-w-2xl flex-col gap-6">
        <div className="flex items-center justify-between">
          <Button asChild variant="glass" size="icon">
            <Link to="/app" aria-label="Voltar">
              <ArrowLeft />
            </Link>
          </Button>
          <ThemeToggle />
        </div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <h1 className="mb-4 flex items-center gap-2 text-2xl font-semibold">
            <Droplets className="size-6 text-primary" />
            Hidratação
          </h1>

          <Card className="glass mb-4 border-0">
            <CardHeader>
              <CardTitle className="text-base">Hoje</CardTitle>
            </CardHeader>
            <CardContent>
              <RegistroAguaCard
                resumo={resumo}
                onAdicionar={onAdicionar}
                onRemover={onRemover}
                onMetaAtualizada={carregar}
                adicionando={adicionando}
                pulseKey={pulseKey}
              />
            </CardContent>
          </Card>

          <Card className="glass mb-4 border-0">
            <CardContent className="flex items-center gap-3 py-4">
              <Flame className="size-5 text-primary" />
              <div>
                <p className="text-sm text-muted-foreground">Sequência de dias consecutivos</p>
                <p className="text-xl font-semibold">
                  {diasConsecutivos} {diasConsecutivos === 1 ? 'dia' : 'dias'}
                </p>
              </div>
            </CardContent>
          </Card>

          <Card className="glass border-0">
            <CardHeader>
              <CardTitle className="text-base">Últimos 7 dias</CardTitle>
            </CardHeader>
            <CardContent>
              <HistoricoAguaChart historico={historico} />
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  )
}
