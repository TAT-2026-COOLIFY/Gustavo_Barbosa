import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, History, Plus } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { TreinoCard } from '@/features/treinos/components/TreinoCard'
import {
  alternarFavoritoTreino,
  duplicarTreino,
  excluirTreino,
  iniciarTreino,
  listarMeusTreinos,
  listarTemplates,
} from '@/features/treinos/api/treinoApi'
import type { TreinoResumoDTO } from '@/types/treino'

export function TreinosListPage() {
  const navigate = useNavigate()
  const [meusTreinos, setMeusTreinos] = useState<TreinoResumoDTO[]>([])
  const [templates, setTemplates] = useState<TreinoResumoDTO[]>([])
  const [carregando, setCarregando] = useState(true)

  async function carregar() {
    const [meus, temp] = await Promise.all([listarMeusTreinos(), listarTemplates()])
    setMeusTreinos(meus)
    setTemplates(temp)
    setCarregando(false)
  }

  useEffect(() => {
    carregar()
  }, [])

  async function handleIniciar(id: number) {
    const sessao = await iniciarTreino(id)
    navigate(`/treinos/execucao/${sessao.id}`)
  }

  async function handleDuplicar(id: number) {
    await duplicarTreino(id)
    carregar()
  }

  async function handleExcluir(id: number) {
    if (!confirm('Excluir este treino? O histórico de execuções dele também será apagado.')) return
    try {
      await excluirTreino(id)
      carregar()
    } catch {
      alert('Não foi possível excluir o treino. Tente novamente.')
    }
  }

  async function handleToggleFavorito(id: number) {
    await alternarFavoritoTreino(id)
    carregar()
  }

  return (
    <div className="relative min-h-svh bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex max-w-3xl flex-col gap-6">
        <div className="flex items-center justify-between">
          <Button asChild variant="glass" size="icon">
            <Link to="/app" aria-label="Voltar">
              <ArrowLeft />
            </Link>
          </Button>
          <div className="flex items-center gap-2">
            <Button asChild variant="glass" size="icon">
              <Link to="/treinos/historico" aria-label="Histórico">
                <History />
              </Link>
            </Button>
            <ThemeToggle />
          </div>
        </div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <div className="mb-4 flex items-center justify-between">
            <h1 className="text-2xl font-semibold">Treinos</h1>
            <Button asChild size="sm">
              <Link to="/treinos/novo">
                <Plus />
                Novo treino
              </Link>
            </Button>
          </div>

          {!carregando && (
            <Tabs defaultValue="meus">
              <TabsList>
                <TabsTrigger value="meus">Meus treinos</TabsTrigger>
                <TabsTrigger value="templates">Treinos prontos</TabsTrigger>
              </TabsList>

              <TabsContent value="meus">
                {meusTreinos.length === 0 ? (
                  <p className="text-sm text-muted-foreground">
                    Você ainda não criou nenhum treino. Crie um novo ou duplique um treino pronto.
                  </p>
                ) : (
                  <div className="grid gap-3 sm:grid-cols-2">
                    {meusTreinos.map((treino) => (
                      <TreinoCard
                        key={treino.id}
                        treino={treino}
                        own
                        onIniciar={() => handleIniciar(treino.id)}
                        onDuplicar={() => handleDuplicar(treino.id)}
                        onEditar={() => navigate(`/treinos/${treino.id}/editar`)}
                        onExcluir={() => handleExcluir(treino.id)}
                        onToggleFavorito={() => handleToggleFavorito(treino.id)}
                      />
                    ))}
                  </div>
                )}
              </TabsContent>

              <TabsContent value="templates">
                <div className="grid gap-3 sm:grid-cols-2">
                  {templates.map((treino) => (
                    <TreinoCard
                      key={treino.id}
                      treino={treino}
                      own={false}
                      onIniciar={() => handleIniciar(treino.id)}
                      onDuplicar={() => handleDuplicar(treino.id)}
                      onToggleFavorito={() => handleToggleFavorito(treino.id)}
                    />
                  ))}
                </div>
              </TabsContent>
            </Tabs>
          )}
        </motion.div>
      </div>
    </div>
  )
}
