import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import { Dumbbell } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { ThemeToggle } from '@/components/shared/ThemeToggle'

export function SplashPage() {
  return (
    <div className="relative flex min-h-svh flex-col items-center justify-center overflow-hidden bg-background px-6">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-60 blur-[90px]" />
        <div className="absolute bottom-[-20%] right-[-5%] h-[26rem] w-[26rem] rounded-full bg-primary opacity-45 blur-[90px]" />
      </div>

      <div className="absolute top-6 right-6">
        <ThemeToggle />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: 'easeOut' }}
        className="glass flex w-full max-w-md flex-col items-center gap-6 rounded-2xl px-8 py-12 text-center shadow-xl"
      >
        <div className="flex size-16 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-lg">
          <Dumbbell className="size-8" />
        </div>

        <div className="space-y-2">
          <h1 className="text-3xl font-semibold tracking-tight">IRONFIT</h1>
          <p className="text-muted-foreground text-balance">
            Gestão inteligente de dietas, treinos e bem-estar em um só lugar.
          </p>
        </div>

        <div className="flex w-full flex-col gap-3 pt-2">
          <Button asChild size="lg">
            <Link to="/cadastro">Criar conta</Link>
          </Button>
          <Button asChild size="lg" variant="outline">
            <Link to="/login">Entrar</Link>
          </Button>
        </div>
      </motion.div>
    </div>
  )
}
