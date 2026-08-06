import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion } from 'framer-motion'
import { Link, useNavigate } from 'react-router-dom'
import { isAxiosError } from 'axios'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { PasswordInput } from '@/components/ui/password-input'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { useAuth } from '@/features/auth/hooks/useAuth'
import { loginSchema, type LoginFormValues } from '@/features/auth/schemas/authSchemas'

export function LoginPage() {
  const { entrar } = useAuth()
  const navigate = useNavigate()
  const [erroGeral, setErroGeral] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema) })

  async function onSubmit(values: LoginFormValues) {
    setErroGeral(null)
    try {
      await entrar(values.email, values.senha)
      navigate('/app', { replace: true })
    } catch (error) {
      if (isAxiosError(error) && error.response?.status === 401) {
        setErroGeral('E-mail ou senha inválidos.')
      } else if (isAxiosError(error) && error.response?.status === 429) {
        setErroGeral('Muitas tentativas. Tente novamente em alguns minutos.')
      } else {
        setErroGeral('Não foi possível entrar. Tente novamente.')
      }
    }
  }

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
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="glass w-full max-w-sm rounded-2xl px-8 py-10 shadow-xl"
      >
        <h1 className="text-2xl font-semibold tracking-tight">Entrar</h1>
        <p className="mt-1 text-sm text-muted-foreground">Acesse sua conta IRONFIT.</p>

        <form className="mt-6 flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="email">E-mail</Label>
            <Input id="email" type="email" autoComplete="email" aria-invalid={!!errors.email} {...register('email')} />
            {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
          </div>

          <div className="flex flex-col gap-1.5">
            <div className="flex items-center justify-between">
              <Label htmlFor="senha">Senha</Label>
              <Link to="/esqueci-senha" className="text-xs text-primary hover:underline">
                Esqueci minha senha
              </Link>
            </div>
            <PasswordInput
              id="senha"
              autoComplete="current-password"
              aria-invalid={!!errors.senha}
              {...register('senha')}
            />
            {errors.senha && <p className="text-xs text-destructive">{errors.senha.message}</p>}
          </div>

          {erroGeral && <p className="text-sm text-destructive">{erroGeral}</p>}

          <Button type="submit" size="lg" className="mt-2" disabled={isSubmitting}>
            {isSubmitting ? 'Entrando...' : 'Entrar'}
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-muted-foreground">
          Não tem conta?{' '}
          <Link to="/cadastro" className="text-primary hover:underline">
            Criar conta
          </Link>
        </p>
      </motion.div>
    </div>
  )
}
