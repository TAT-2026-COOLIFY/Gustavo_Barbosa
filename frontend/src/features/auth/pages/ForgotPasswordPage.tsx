import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { esqueciSenha } from '@/features/auth/api/authApi'
import { esqueciSenhaSchema, type EsqueciSenhaFormValues } from '@/features/auth/schemas/authSchemas'

export function ForgotPasswordPage() {
  const [enviado, setEnviado] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<EsqueciSenhaFormValues>({ resolver: zodResolver(esqueciSenhaSchema) })

  async function onSubmit(values: EsqueciSenhaFormValues) {
    await esqueciSenha(values.email).catch(() => undefined)
    // Mesma resposta visual haja ou não conta com esse e-mail — evita confirmar cadastro a terceiros.
    setEnviado(true)
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
        <h1 className="text-2xl font-semibold tracking-tight">Esqueci minha senha</h1>

        {enviado ? (
          <>
            <p className="mt-3 text-sm text-muted-foreground">
              Se houver uma conta com esse e-mail, enviamos um link de redefinição de senha.
            </p>
            <Button asChild variant="outline" className="mt-6 w-full">
              <Link to="/login">
                <ArrowLeft />
                Voltar para o login
              </Link>
            </Button>
          </>
        ) : (
          <>
            <p className="mt-1 text-sm text-muted-foreground">
              Informe seu e-mail para receber o link de redefinição.
            </p>
            <form className="mt-6 flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)} noValidate>
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="email">E-mail</Label>
                <Input
                  id="email"
                  type="email"
                  autoComplete="email"
                  aria-invalid={!!errors.email}
                  {...register('email')}
                />
                {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
              </div>

              <Button type="submit" size="lg" className="mt-2" disabled={isSubmitting}>
                {isSubmitting ? 'Enviando...' : 'Enviar link'}
              </Button>
            </form>
            <p className="mt-6 text-center text-sm text-muted-foreground">
              <Link to="/login" className="text-primary hover:underline">
                Voltar para o login
              </Link>
            </p>
          </>
        )}
      </motion.div>
    </div>
  )
}
