import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  Apple,
  Bell,
  Droplets,
  Dumbbell,
  Flame,
  LineChart,
  LogOut,
  Moon,
  Scale,
  Sparkles,
  Target,
  Trophy,
  User,
  Wind,
} from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { useAuth } from '@/features/auth/hooks/useAuth'
import { obterDashboard } from '@/features/dashboard/api/dashboardApi'
import { StatCard } from '@/features/dashboard/components/StatCard'
import { CaloriasChart } from '@/features/dashboard/components/CaloriasChart'
import { ConquistaBadge } from '@/features/dashboard/components/ConquistaBadge'
import { getHistorico } from '@/features/hidratacao/api/hidratacaoApi'
import { HistoricoAguaChart } from '@/features/hidratacao/components/HistoricoAguaChart'
import { listarProgresso } from '@/features/progresso/api/progressoApi'
import { PesoChart } from '@/features/progresso/components/PesoChart'
import { historicoDiario } from '@/features/dieta/api/diarioApi'
import { MacroProgressBar } from '@/features/dieta/components/MacroProgressBar'
import type { DashboardResponseDTO } from '@/types/dashboard'
import type { DiarioHistoricoDiaDTO } from '@/types/dieta'
import type { HistoricoDiaHidratacaoDTO } from '@/types/hidratacao'
import type { TipoObjetivo } from '@/types/perfil'
import type { RegistroProgressoResponseDTO } from '@/types/progresso'

const LABELS_OBJETIVO: Record<TipoObjetivo, string> = {
  EMAGRECIMENTO: 'Emagrecimento',
  HIPERTROFIA: 'Hipertrofia',
  MANUTENCAO: 'Manutenção',
  CONDICIONAMENTO: 'Condicionamento',
  SAUDE: 'Saúde',
}

function classificarImc(imc: number): string {
  if (imc < 18.5) return 'Abaixo do peso'
  if (imc < 25) return 'Peso normal'
  if (imc < 30) return 'Sobrepeso'
  return 'Obesidade'
}

export function DashboardPage() {
  const { usuario, sair } = useAuth()
  const [dashboard, setDashboard] = useState<DashboardResponseDTO | null>(null)
  const [aguaHistorico, setAguaHistorico] = useState<HistoricoDiaHidratacaoDTO[]>([])
  const [pesoHistorico, setPesoHistorico] = useState<RegistroProgressoResponseDTO[]>([])
  const [caloriasHistorico, setCaloriasHistorico] = useState<DiarioHistoricoDiaDTO[]>([])
  const [carregando, setCarregando] = useState(true)

  useEffect(() => {
    Promise.all([obterDashboard(), getHistorico(7), listarProgresso(), historicoDiario(7)]).then(
      ([resumo, agua, peso, calorias]) => {
        setDashboard(resumo)
        setAguaHistorico(agua)
        setPesoHistorico(peso.slice(0, 10)) // listarProgresso() vem ordenado do mais recente para o mais antigo
        setCaloriasHistorico(calorias)
        setCarregando(false)
      },
    )
  }, [])

  return (
    <div className="relative min-h-svh bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex max-w-4xl flex-col gap-6">
        <div className="flex items-center justify-between">
          <div />
          <div className="flex items-center gap-2">
            <ThemeToggle />
            <Button asChild variant="glass" size="icon" className="relative">
              <Link to="/notificacoes" aria-label="Notificações">
                <Bell />
                {dashboard && dashboard.notificacoesNaoLidas > 0 && (
                  <span className="absolute -top-1 -right-1 flex size-4 items-center justify-center rounded-full bg-destructive text-[10px] font-medium text-destructive-foreground">
                    {dashboard.notificacoesNaoLidas > 9 ? '9+' : dashboard.notificacoesNaoLidas}
                  </span>
                )}
              </Link>
            </Button>
            <Button asChild variant="glass" size="icon">
              <Link to="/treinos" aria-label="Treinos">
                <Dumbbell />
              </Link>
            </Button>
            <Button asChild variant="glass" size="icon">
              <Link to="/dietas" aria-label="Dietas">
                <Apple />
              </Link>
            </Button>
            <Button asChild variant="glass" size="icon">
              <Link to="/hidratacao" aria-label="Hidratação">
                <Droplets />
              </Link>
            </Button>
            <Button asChild variant="glass" size="icon">
              <Link to="/progresso" aria-label="Progresso">
                <LineChart />
              </Link>
            </Button>
            <Button asChild variant="glass" size="icon" className="relative">
              <Link to="/ia" aria-label="Assistente IA">
                <Sparkles />
                {dashboard?.rascunhoIaPendente && (
                  <span className="absolute top-1 right-1.5 size-2 rounded-full bg-primary" />
                )}
              </Link>
            </Button>
            <Button asChild variant="glass" size="icon">
              <Link to="/meditacao" aria-label="Meditação">
                <Wind />
              </Link>
            </Button>
            <Button asChild variant="glass" size="icon">
              <Link to="/perfil" aria-label="Meu perfil">
                <User />
              </Link>
            </Button>
            <Button variant="glass" size="icon" aria-label="Sair" onClick={() => sair()}>
              <LogOut />
            </Button>
          </div>
        </div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <h1 className="text-2xl font-semibold">Olá, {usuario?.nome}!</h1>
          <p className="text-sm text-muted-foreground">
            {new Date().toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long' })}
          </p>
        </motion.div>

        {!carregando && dashboard && (
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, delay: 0.1 }}
            className="flex flex-col gap-6"
          >
            <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
              <StatCard
                icon={<Scale className="size-4" />}
                label="IMC"
                valor={dashboard.imc ? dashboard.imc.imc : '—'}
                sublabel={
                  dashboard.imc ? (
                    `${dashboard.imc.pesoKg}kg · ${classificarImc(dashboard.imc.imc ?? 0)}`
                  ) : (
                    <Link to="/progresso" className="underline">
                      Registrar peso
                    </Link>
                  )
                }
              />
              <StatCard
                icon={<Target className="size-4" />}
                label="Objetivo"
                valor={dashboard.objetivoAtivo ? LABELS_OBJETIVO[dashboard.objetivoAtivo.tipo] : '—'}
                sublabel={
                  dashboard.objetivoAtivo?.pesoMetaKg ? (
                    `Meta: ${dashboard.objetivoAtivo.pesoMetaKg}kg`
                  ) : (
                    <Link to="/perfil" className="underline">
                      Definir objetivo
                    </Link>
                  )
                }
              />
              <StatCard
                icon={<Dumbbell className="size-4" />}
                label="Treino"
                valor={dashboard.treino.treinouHoje ? 'Treinou hoje' : 'Ainda não'}
                sublabel={`${dashboard.treino.volumeSemanalKg}kg essa semana`}
              />
              <StatCard
                icon={<Wind className="size-4" />}
                label="Meditação"
                valor={`${dashboard.meditacao.diasConsecutivos} dias`}
                sublabel="sequência atual"
              />
            </div>

            <Card className="glass border-0">
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base">
                  <Droplets className="size-4" />
                  Água hoje
                </CardTitle>
              </CardHeader>
              <CardContent className="flex flex-col gap-4">
                <MacroProgressBar label="Consumo" consumido={dashboard.agua.totalMl} meta={dashboard.agua.metaMl} unidade="ml" />
                <p className="text-xs text-muted-foreground">{dashboard.agua.diasConsecutivos} dias seguidos batendo a meta</p>
                <HistoricoAguaChart historico={aguaHistorico} />
              </CardContent>
            </Card>

            <Card className="glass border-0">
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base">
                  <Apple className="size-4" />
                  {dashboard.dieta.temDietaAtiva ? dashboard.dieta.dietaNome : 'Dieta de hoje'}
                </CardTitle>
              </CardHeader>
              <CardContent className="flex flex-col gap-4">
                {dashboard.dieta.temDietaAtiva ? (
                  <>
                    <MacroProgressBar
                      label="Calorias"
                      consumido={dashboard.dieta.caloriasConsumidas ?? 0}
                      meta={dashboard.dieta.metaCaloriasKcal ?? 0}
                      unidade="kcal"
                    />
                    <MacroProgressBar
                      label="Proteína"
                      consumido={dashboard.dieta.proteinaConsumidaG ?? 0}
                      meta={dashboard.dieta.metaProteinaG ?? 0}
                      unidade="g"
                    />
                    <MacroProgressBar
                      label="Carboidrato"
                      consumido={dashboard.dieta.carboidratoConsumidoG ?? 0}
                      meta={dashboard.dieta.metaCarboidratoG ?? 0}
                      unidade="g"
                    />
                    <MacroProgressBar
                      label="Gordura"
                      consumido={dashboard.dieta.gorduraConsumidaG ?? 0}
                      meta={dashboard.dieta.metaGorduraG ?? 0}
                      unidade="g"
                    />
                  </>
                ) : (
                  <p className="text-sm text-muted-foreground">
                    Você ainda não tem uma dieta ativa.{' '}
                    <Link to="/dietas" className="underline">
                      Ative uma dieta
                    </Link>{' '}
                    para acompanhar suas calorias aqui.
                  </p>
                )}
                <div>
                  <p className="mb-1 flex items-center gap-1.5 text-xs font-medium text-muted-foreground">
                    <Flame className="size-3.5" />
                    Calorias nos últimos 7 dias
                  </p>
                  <CaloriasChart historico={caloriasHistorico} />
                </div>
              </CardContent>
            </Card>

            <Card className="glass border-0">
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base">
                  <LineChart className="size-4" />
                  Evolução de peso
                </CardTitle>
              </CardHeader>
              <CardContent>
                <PesoChart registros={pesoHistorico} pesoMetaKg={dashboard.objetivoAtivo?.pesoMetaKg} />
              </CardContent>
            </Card>

            <Card className="glass border-0">
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base">
                  <Trophy className="size-4" />
                  Conquistas
                </CardTitle>
              </CardHeader>
              <CardContent>
                {dashboard.conquistas.length === 0 ? (
                  <p className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Moon className="size-4" />
                    Continue evoluindo para desbloquear conquistas.
                  </p>
                ) : (
                  <div className="grid gap-2 sm:grid-cols-2">
                    {dashboard.conquistas.map((conquista) => (
                      <ConquistaBadge key={conquista.titulo} conquista={conquista} />
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </motion.div>
        )}
      </div>
    </div>
  )
}
