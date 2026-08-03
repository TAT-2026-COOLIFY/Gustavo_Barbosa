import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion } from 'framer-motion'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { redefinirSenha } from '@/features/auth/api/authApi'
import { redefinirSenhaSchema, type RedefinirSenhaFormValues } from '@/features/auth/schemas/authSchemas'

export function ResetPasswordPage() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')
  const navigate = useNavigate()
  const [erroGeral, setErroGeral] = useState<string | null>(null)
  const [sucesso, setSucesso] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RedefinirSenhaFormValues>({ resolver: zodResolver(redefinirSenhaSchema) })

  async function onSubmit(values: RedefinirSenhaFormValues) {
    if (!token) return
    setErroGeral(null)
    try {
      await redefinirSenha({ token, novaSenha: values.novaSenha })
      setSucesso(true)
      setTimeout(() => navigate('/login', { replace: true }), 2000)
    } catch {
      setErroGeral('Link inválido ou expirado. Solicite um novo.')
    }
  }

  return (
    <div className="relative flex min-h-svh flex-col items-center justify-center overflow-hidden bg-background px-6">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-60 blur-[90px]" />
      </div>

      <div className="absolute top-6 right-6">
        <ThemeToggle />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="glass w-full max-w-sm rounded-2xl px-8 py-10 shadow-xl"
      >
        <h1 className="text-2xl font-semibold tracking-tight">Redefinir senha</h1>

        {!token ? (
          <>
            <p className="mt-3 text-sm text-destructive">Link inválido: token não informado.</p>
            <Button asChild variant="outline" className="mt-6 w-full">
              <Link to="/esqueci-senha">
                <ArrowLeft />
                Solicitar novo link
              </Link>
            </Button>
          </>
        ) : sucesso ? (
          <p className="mt-3 text-sm text-muted-foreground">Senha redefinida! Redirecionando para o login...</p>
        ) : (
          <form className="mt-6 flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="novaSenha">Nova senha</Label>
              <Input
                id="novaSenha"
                type="password"
                autoComplete="new-password"
                aria-invalid={!!errors.novaSenha}
                {...register('novaSenha')}
              />
              {errors.novaSenha && <p className="text-xs text-destructive">{errors.novaSenha.message}</p>}
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="confirmarSenha">Confirmar nova senha</Label>
              <Input
                id="confirmarSenha"
                type="password"
                autoComplete="new-password"
                aria-invalid={!!errors.confirmarSenha}
                {...register('confirmarSenha')}
              />
              {errors.confirmarSenha && <p className="text-xs text-destructive">{errors.confirmarSenha.message}</p>}
            </div>

            {erroGeral && <p className="text-sm text-destructive">{erroGeral}</p>}

            <Button type="submit" size="lg" className="mt-2" disabled={isSubmitting}>
              {isSubmitting ? 'Salvando...' : 'Redefinir senha'}
            </Button>
          </form>
        )}
      </motion.div>
    </div>
  )
}
