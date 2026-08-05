import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, Flame, History } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { MeditacaoCard } from '@/features/meditacao/components/MeditacaoCard'
import { iniciarMeditacao, listarMeditacoes, sequenciaMeditacao } from '@/features/meditacao/api/meditacaoApi'
import type { MeditacaoResponseDTO, TipoMeditacao } from '@/types/meditacao'

const TIPOS: { valor: TipoMeditacao | 'TODOS'; label: string }[] = [
  { valor: 'TODOS', label: 'Todas' },
  { valor: 'RESPIRACAO', label: 'Respiração' },
  { valor: 'RELAXAMENTO', label: 'Relaxamento' },
  { valor: 'FOCO', label: 'Foco' },
  { valor: 'SONO', label: 'Sono' },
  { valor: 'ANSIEDADE', label: 'Ansiedade' },
]

export function MeditacoesListPage() {
  const navigate = useNavigate()
  const [meditacoes, setMeditacoes] = useState<MeditacaoResponseDTO[]>([])
  const [tipo, setTipo] = useState<TipoMeditacao | 'TODOS'>('TODOS')
  const [diasConsecutivos, setDiasConsecutivos] = useState(0)
  const [iniciandoId, setIniciandoId] = useState<number | null>(null)

  useEffect(() => {
    listarMeditacoes(tipo === 'TODOS' ? undefined : tipo).then(setMeditacoes)
  }, [tipo])

  useEffect(() => {
    sequenciaMeditacao().then((s) => setDiasConsecutivos(s.diasConsecutivos))
  }, [])

  async function onIniciar(meditacaoId: number) {
    setIniciandoId(meditacaoId)
    try {
      const sessao = await iniciarMeditacao(meditacaoId)
      navigate(`/meditacao/execucao/${sessao.id}`)
    } finally {
      setIniciandoId(null)
    }
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
          <div className="flex items-center gap-2">
            <Button asChild variant="glass" size="icon">
              <Link to="/meditacao/historico" aria-label="Histórico">
                <History />
              </Link>
            </Button>
            <ThemeToggle />
          </div>
        </div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <h1 className="mb-4 text-2xl font-semibold">Meditação</h1>

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

          <Tabs value={tipo} onValueChange={(v) => setTipo(v as TipoMeditacao | 'TODOS')} className="mb-4">
            <TabsList>
              {TIPOS.map((t) => (
                <TabsTrigger key={t.valor} value={t.valor}>
                  {t.label}
                </TabsTrigger>
              ))}
            </TabsList>
          </Tabs>

          <div className="flex flex-col gap-3">
            {meditacoes.map((meditacao) => (
              <MeditacaoCard
                key={meditacao.id}
                meditacao={meditacao}
                onIniciar={onIniciar}
                iniciando={iniciandoId === meditacao.id}
              />
            ))}
          </div>
        </motion.div>
      </div>
    </div>
  )
}
