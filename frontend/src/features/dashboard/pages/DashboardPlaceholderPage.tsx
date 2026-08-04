import { motion } from 'framer-motion'
import { Apple, Dumbbell, LogOut, User } from 'lucide-react'
import { Link } from 'react-router-dom'

import { Button } from '@/components/ui/button'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { useAuth } from '@/features/auth/hooks/useAuth'

export function DashboardPlaceholderPage() {
  const { usuario, sair } = useAuth()

  return (
    <div className="relative flex min-h-svh flex-col items-center justify-center gap-6 bg-background px-6">
      <div className="absolute top-6 right-6 flex items-center gap-2">
        <ThemeToggle />
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
          <Link to="/perfil" aria-label="Meu perfil">
            <User />
          </Link>
        </Button>
        <Button variant="glass" size="icon" aria-label="Sair" onClick={() => sair()}>
          <LogOut />
        </Button>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="glass flex flex-col items-center gap-2 rounded-2xl px-10 py-12 text-center shadow-xl"
      >
        <h1 className="text-2xl font-semibold">Olá, {usuario?.nome}!</h1>
        <p className="max-w-sm text-muted-foreground">
          O Dashboard completo (IMC, treino do dia, calorias, gráficos) será implementado na Fase 8. Por enquanto,
          esta tela confirma que sua sessão está autenticada.
        </p>
      </motion.div>
    </div>
  )
}
