import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, NotebookText, Plus } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { DietaCard } from '@/features/dieta/components/DietaCard'
import {
  alternarFavoritoDieta,
  ativarDieta,
  duplicarDieta,
  excluirDieta,
  listarMinhasDietas,
  listarTemplates,
} from '@/features/dieta/api/dietaApi'
import type { DietaResumoDTO } from '@/types/dieta'

export function DietasListPage() {
  const navigate = useNavigate()
  const [minhasDietas, setMinhasDietas] = useState<DietaResumoDTO[]>([])
  const [templates, setTemplates] = useState<DietaResumoDTO[]>([])
  const [carregando, setCarregando] = useState(true)
  const [aba, setAba] = useState('minhas')
  const [aviso, setAviso] = useState<string | null>(null)
  const [ativando, setAtivando] = useState(false)

  async function carregar() {
    const [minhas, temp] = await Promise.all([listarMinhasDietas(), listarTemplates()])
    setMinhasDietas(minhas)
    setTemplates(temp)
    setCarregando(false)
  }

  useEffect(() => {
    carregar()
  }, [])

  async function handleAtivar(id: number) {
    if (ativando) return
    setAtivando(true)
    try {
      const dieta = await ativarDieta(id)
      await carregar()
      // Dieta pronta é ativada como uma cópia em "Minhas dietas" — leva o usuário até lá para ver o resultado.
      setAba('minhas')
      setAviso(`"${dieta.nome}" agora é sua dieta ativa.`)
    } catch {
      alert('Não foi possível ativar a dieta. Tente novamente.')
    } finally {
      setAtivando(false)
    }
  }

  async function handleDuplicar(id: number) {
    await duplicarDieta(id)
    carregar()
  }

  async function handleExcluir(id: number) {
    if (!confirm('Excluir esta dieta?')) return
    await excluirDieta(id)
    carregar()
  }

  async function handleToggleFavorito(id: number) {
    await alternarFavoritoDieta(id)
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
              <Link to="/dietas/diario" aria-label="Diário alimentar">
                <NotebookText />
              </Link>
            </Button>
            <ThemeToggle />
          </div>
        </div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <div className="mb-4 flex items-center justify-between">
            <h1 className="text-2xl font-semibold">Dietas</h1>
            <Button asChild size="sm">
              <Link to="/dietas/novo">
                <Plus />
                Nova dieta
              </Link>
            </Button>
          </div>

          {!carregando && (
            <Tabs value={aba} onValueChange={setAba}>
              <TabsList>
                <TabsTrigger value="minhas">Minhas dietas</TabsTrigger>
                <TabsTrigger value="templates">Dietas prontas</TabsTrigger>
              </TabsList>

              <TabsContent value="minhas">
                {aviso && (
                  <p className="mb-3 rounded-lg border border-primary/40 bg-primary/10 p-3 text-sm">{aviso}</p>
                )}
                {minhasDietas.length === 0 ? (
                  <p className="text-sm text-muted-foreground">
                    Você ainda não criou nenhuma dieta. Crie uma nova ou duplique uma dieta pronta.
                  </p>
                ) : (
                  <div className="grid gap-3 sm:grid-cols-2">
                    {minhasDietas.map((dieta) => (
                      <DietaCard
                        key={dieta.id}
                        dieta={dieta}
                        own
                        ativando={ativando}
                        onAtivar={() => handleAtivar(dieta.id)}
                        onDuplicar={() => handleDuplicar(dieta.id)}
                        onEditar={() => navigate(`/dietas/${dieta.id}/editar`)}
                        onExcluir={() => handleExcluir(dieta.id)}
                        onToggleFavorito={() => handleToggleFavorito(dieta.id)}
                      />
                    ))}
                  </div>
                )}
              </TabsContent>

              <TabsContent value="templates">
                <div className="grid gap-3 sm:grid-cols-2">
                  {templates.map((dieta) => (
                    <DietaCard
                      key={dieta.id}
                      dieta={dieta}
                      own={false}
                      ativando={ativando}
                      onAtivar={() => handleAtivar(dieta.id)}
                      onDuplicar={() => handleDuplicar(dieta.id)}
                      onToggleFavorito={() => handleToggleFavorito(dieta.id)}
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
