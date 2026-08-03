import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { ThemeToggle } from '@/components/shared/ThemeToggle'

export function ComingSoonPage({ title }: { title: string }) {
  return (
    <div className="relative flex min-h-svh flex-col items-center justify-center gap-6 bg-background px-6">
      <div className="absolute top-6 right-6">
        <ThemeToggle />
      </div>
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="glass flex flex-col items-center gap-4 rounded-2xl px-10 py-12 text-center shadow-xl"
      >
        <h1 className="text-2xl font-semibold">{title}</h1>
        <p className="text-muted-foreground max-w-sm">
          Esta tela será implementada nas próximas fases do desenvolvimento do IRONFIT.
        </p>
        <Button asChild variant="outline">
          <Link to="/">
            <ArrowLeft />
            Voltar
          </Link>
        </Button>
      </motion.div>
    </div>
  )
}
