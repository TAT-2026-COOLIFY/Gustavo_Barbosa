//Apresenta o formulário de criação de conta.

import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion } from 'framer-motion'
import { Link, useNavigate } from 'react-router-dom'
import { isAxiosError } from 'axios'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { useAuth } from '@/features/auth/hooks/useAuth'
import { cadastroSchema, type CadastroFormValues } from '@/features/auth/schemas/authSchemas'

export function CadastroPage() {
  const { cadastrar } = useAuth()
  const navigate = useNavigate()
  const [erroGeral, setErroGeral] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<CadastroFormValues>({ resolver: zodResolver(cadastroSchema) })

  async function onSubmit(values: CadastroFormValues) {
    setErroGeral(null)
    try {
      await cadastrar(values.nome, values.email, values.senha)
      navigate('/app', { replace: true })
    } catch (error) {
      if (isAxiosError(error) && error.response?.status === 409) {
        setErroGeral('Este e-mail já está cadastrado.')
      } else {
        setErroGeral('Não foi possível criar sua conta. Tente novamente.')
      }
    }
  }

  return (
    <div className="relative flex min-h-svh flex-col items-center justify-center overflow-hidden bg-background px-6 py-12">
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
        <h1 className="text-2xl font-semibold tracking-tight">Criar conta</h1>
        <p className="mt-1 text-sm text-muted-foreground">Comece sua jornada no IRONFIT.</p>

        <form className="mt-6 flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="nome">Nome</Label>
            <Input id="nome" autoComplete="name" aria-invalid={!!errors.nome} {...register('nome')} />
            {errors.nome && <p className="text-xs text-destructive">{errors.nome.message}</p>}
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="email">E-mail</Label>
            <Input id="email" type="email" autoComplete="email" aria-invalid={!!errors.email} {...register('email')} />
            {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="senha">Senha</Label>
            <Input
              id="senha"
              type="password"
              autoComplete="new-password"
              aria-invalid={!!errors.senha}
              {...register('senha')}
            />
            {errors.senha && <p className="text-xs text-destructive">{errors.senha.message}</p>}
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="confirmarSenha">Confirmar senha</Label>
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
            {isSubmitting ? 'Criando conta...' : 'Criar conta'}
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-muted-foreground">
          Já tem conta?{' '}
          <Link to="/login" className="text-primary hover:underline">
            Entrar
          </Link>
        </p>
      </motion.div>
    </div>
  )
}
