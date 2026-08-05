import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, Flame } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { formatarTempo } from '@/features/treinos/hooks/useElapsedSeconds'
import { historicoMeditacao, sequenciaMeditacao } from '@/features/meditacao/api/meditacaoApi'
import type { HistoricoMeditacaoResponseDTO } from '@/types/meditacao'

const LABELS_TIPO: Record<string, string> = {
  RESPIRACAO: 'Respiração',
  RELAXAMENTO: 'Relaxamento',
  FOCO: 'Foco',
  SONO: 'Sono',
  ANSIEDADE: 'Ansiedade',
}

export function HistoricoMeditacaoPage() {
  const [historico, setHistorico] = useState<HistoricoMeditacaoResponseDTO[]>([])
  const [diasConsecutivos, setDiasConsecutivos] = useState(0)

  useEffect(() => {
    historicoMeditacao().then(setHistorico)
    sequenciaMeditacao().then((s) => setDiasConsecutivos(s.diasConsecutivos))
  }, [])

  return (
    <div className="relative min-h-svh bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex max-w-2xl flex-col gap-6">
        <div className="flex items-center justify-between">
          <Button asChild variant="glass" size="icon">
            <Link to="/meditacao" aria-label="Voltar">
              <ArrowLeft />
            </Link>
          </Button>
          <ThemeToggle />
        </div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <h1 className="mb-4 text-2xl font-semibold">Histórico</h1>

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

          {historico.length === 0 ? (
            <p className="text-sm text-muted-foreground">Nenhuma meditação concluída ainda.</p>
          ) : (
            <div className="flex flex-col gap-3">
              {historico.map((sessao) => (
                <Card key={sessao.id} className="glass border-0">
                  <CardContent className="flex items-center justify-between py-4">
                    <div>
                      <p className="font-medium">
                        {sessao.meditacaoNome}{' '}
                        <span className="font-normal text-muted-foreground">· {LABELS_TIPO[sessao.tipo]}</span>
                      </p>
                      <p className="text-sm text-muted-foreground">
                        {new Date(sessao.dataRealizacao).toLocaleString('pt-BR')}
                        {sessao.duracaoSegundos ? ` · ${formatarTempo(sessao.duracaoSegundos)}` : ''}
                      </p>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </motion.div>
      </div>
    </div>
  )
}
