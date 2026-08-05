import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, CheckCheck } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { NotificacaoItem } from '@/features/notificacoes/components/NotificacaoItem'
import {
  listarNotificacoes,
  marcarNotificacaoLida,
  marcarTodasNotificacoesLidas,
  removerNotificacao,
} from '@/features/notificacoes/api/notificacaoApi'
import type { NotificacaoResponseDTO } from '@/types/notificacao'

export function NotificacoesPage() {
  const [notificacoes, setNotificacoes] = useState<NotificacaoResponseDTO[] | null>(null)

  useEffect(() => {
    listarNotificacoes().then(setNotificacoes)
  }, [])

  async function onMarcarLida(id: number) {
    const atualizada = await marcarNotificacaoLida(id)
    setNotificacoes((atual) => (atual ?? []).map((n) => (n.id === id ? atualizada : n)))
  }

  async function onRemover(id: number) {
    await removerNotificacao(id)
    setNotificacoes((atual) => (atual ?? []).filter((n) => n.id !== id))
  }

  async function onMarcarTodasLidas() {
    await marcarTodasNotificacoesLidas()
    setNotificacoes((atual) => (atual ?? []).map((n) => ({ ...n, lida: true })))
  }

  const temNaoLidas = (notificacoes ?? []).some((n) => !n.lida)

  if (!notificacoes) {
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
          <div className="mb-4 flex items-center justify-between">
            <h1 className="text-2xl font-semibold">Notificações</h1>
            {temNaoLidas && (
              <Button variant="outline" size="sm" onClick={onMarcarTodasLidas}>
                <CheckCheck className="size-4" />
                Marcar todas como lidas
              </Button>
            )}
          </div>

          {notificacoes.length === 0 ? (
            <p className="text-sm text-muted-foreground">Nenhuma notificação por enquanto.</p>
          ) : (
            <div className="flex flex-col gap-3">
              {notificacoes.map((notificacao) => (
                <NotificacaoItem
                  key={notificacao.id}
                  notificacao={notificacao}
                  onMarcarLida={onMarcarLida}
                  onRemover={onRemover}
                />
              ))}
            </div>
          )}
        </motion.div>
      </div>
    </div>
  )
}
