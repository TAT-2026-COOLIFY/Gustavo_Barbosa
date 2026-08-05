import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, LineChart } from 'lucide-react'
import { isAxiosError } from 'axios'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { getObjetivoAtivo } from '@/features/perfil/api/perfilApi'
import { ComparacaoFotosCard } from '@/features/progresso/components/ComparacaoFotosCard'
import { HistoricoProgressoList } from '@/features/progresso/components/HistoricoProgressoList'
import { PesoChart } from '@/features/progresso/components/PesoChart'
import { RegistroProgressoDialog } from '@/features/progresso/components/RegistroProgressoDialog'
import { listarProgresso, removerProgresso } from '@/features/progresso/api/progressoApi'
import type { RegistroProgressoResponseDTO } from '@/types/progresso'

export function ProgressoPage() {
  const [registros, setRegistros] = useState<RegistroProgressoResponseDTO[] | null>(null)
  const [pesoMetaKg, setPesoMetaKg] = useState<number | null>(null)

  useEffect(() => {
    listarProgresso().then(setRegistros)
    getObjetivoAtivo()
      .then((objetivo) => setPesoMetaKg(objetivo.pesoMetaKg))
      .catch((error) => {
        if (isAxiosError(error) && error.response?.status === 404) {
          setPesoMetaKg(null)
        }
      })
  }, [])

  function onSalvo(registro: RegistroProgressoResponseDTO) {
    setRegistros((atual) => {
      const lista = atual ?? []
      const semEsse = lista.filter((r) => r.id !== registro.id)
      return [registro, ...semEsse].sort((a, b) => b.dataRegistro.localeCompare(a.dataRegistro))
    })
  }

  async function onRemover(id: number) {
    await removerProgresso(id)
    setRegistros((atual) => (atual ?? []).filter((r) => r.id !== id))
  }

  if (!registros) {
    return (
      <div className="flex min-h-svh items-center justify-center bg-background">
        <div className="size-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }

  const atual = registros[0]
  const maisAntigo = registros[registros.length - 1]
  const variacaoKg = atual && maisAntigo && atual.id !== maisAntigo.id ? atual.pesoKg - maisAntigo.pesoKg : null

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
          <div className="mb-4 flex items-center justify-between">
            <h1 className="flex items-center gap-2 text-2xl font-semibold">
              <LineChart className="size-6 text-primary" />
              Progresso
            </h1>
            <RegistroProgressoDialog onSalvo={onSalvo} />
          </div>

          {atual && (
            <div className="mb-4 grid grid-cols-3 gap-3">
              <Card className="glass border-0">
                <CardContent className="py-4 text-center">
                  <p className="text-xs text-muted-foreground">Peso atual</p>
                  <p className="text-xl font-semibold">{atual.pesoKg}kg</p>
                </CardContent>
              </Card>
              <Card className="glass border-0">
                <CardContent className="py-4 text-center">
                  <p className="text-xs text-muted-foreground">IMC</p>
                  <p className="text-xl font-semibold">{atual.imc ?? '—'}</p>
                </CardContent>
              </Card>
              <Card className="glass border-0">
                <CardContent className="py-4 text-center">
                  <p className="text-xs text-muted-foreground">Variação</p>
                  <p className="text-xl font-semibold">
                    {variacaoKg === null ? '—' : `${variacaoKg > 0 ? '+' : ''}${variacaoKg.toFixed(1)}kg`}
                  </p>
                </CardContent>
              </Card>
            </div>
          )}

          <Card className="glass mb-4 border-0">
            <CardHeader>
              <CardTitle className="text-base">Evolução do peso</CardTitle>
            </CardHeader>
            <CardContent>
              <PesoChart registros={registros} pesoMetaKg={pesoMetaKg} />
            </CardContent>
          </Card>

          <Card className="glass mb-4 border-0">
            <CardHeader>
              <CardTitle className="text-base">Fotos: antes e agora</CardTitle>
            </CardHeader>
            <CardContent>
              <ComparacaoFotosCard registros={registros} />
            </CardContent>
          </Card>

          <Card className="glass border-0">
            <CardHeader>
              <CardTitle className="text-base">Histórico</CardTitle>
            </CardHeader>
            <CardContent>
              <HistoricoProgressoList registros={registros} onAtualizado={onSalvo} onRemover={onRemover} />
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  )
}
