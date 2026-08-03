import { useEffect, useState } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import { isAxiosError } from 'axios'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Textarea } from '@/components/ui/textarea'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { AvatarUpload } from '@/features/perfil/components/AvatarUpload'
import { ObjetivoCard } from '@/features/perfil/components/ObjetivoCard'
import { atualizarPerfil, getObjetivoAtivo, getPerfil } from '@/features/perfil/api/perfilApi'
import { atualizarPerfilSchema, type AtualizarPerfilFormValues } from '@/features/perfil/schemas/perfilSchemas'
import type { ObjetivoResponseDTO, PerfilResponseDTO } from '@/types/perfil'

const LABELS_SEXO: Record<string, string> = { MASCULINO: 'Masculino', FEMININO: 'Feminino', OUTRO: 'Outro' }
const LABELS_NIVEL: Record<string, string> = {
  INICIANTE: 'Iniciante',
  INTERMEDIARIO: 'Intermediário',
  AVANCADO: 'Avançado',
}

export function PerfilPage() {
  const [perfil, setPerfil] = useState<PerfilResponseDTO | null>(null)
  const [objetivoAtivo, setObjetivoAtivo] = useState<ObjetivoResponseDTO | null>(null)
  const [salvo, setSalvo] = useState(false)

  const {
    control,
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<AtualizarPerfilFormValues>({ resolver: zodResolver(atualizarPerfilSchema) })

  useEffect(() => {
    getPerfil().then((data) => {
      setPerfil(data)
      reset({
        nome: data.nome,
        dataNascimento: data.dataNascimento ?? '',
        sexo: data.sexo ?? undefined,
        alturaCm: data.alturaCm ? String(data.alturaCm) : '',
        nivelExperiencia: data.nivelExperiencia ?? undefined,
        bio: data.bio ?? '',
      })
    })

    getObjetivoAtivo()
      .then(setObjetivoAtivo)
      .catch((error) => {
        if (isAxiosError(error) && error.response?.status === 404) {
          setObjetivoAtivo(null)
        }
      })
  }, [reset])

  async function onSubmit(values: AtualizarPerfilFormValues) {
    setSalvo(false)
    const atualizado = await atualizarPerfil({
      nome: values.nome,
      dataNascimento: values.dataNascimento || null,
      sexo: values.sexo ?? null,
      alturaCm: values.alturaCm ? Number(values.alturaCm) : null,
      nivelExperiencia: values.nivelExperiencia ?? null,
      bio: values.bio || null,
    })
    setPerfil(atualizado)
    setSalvo(true)
    setTimeout(() => setSalvo(false), 3000)
  }

  if (!perfil) {
    return (
      <div className="flex min-h-svh items-center justify-center bg-background">
        <div className="size-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <div className="relative min-h-svh bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-40 blur-[110px]" />
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
          <Card className="glass border-0">
            <CardHeader>
              <CardTitle>Meu perfil</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-6">
              <AvatarUpload
                nome={perfil.nome}
                fotoPerfilUrl={perfil.fotoPerfilUrl}
                onUploaded={(url) => setPerfil({ ...perfil, fotoPerfilUrl: url })}
              />

              <form className="flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)} noValidate>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="nome">Nome</Label>
                  <Input id="nome" aria-invalid={!!errors.nome} {...register('nome')} />
                  {errors.nome && <p className="text-xs text-destructive">{errors.nome.message}</p>}
                </div>

                <div className="flex flex-col gap-1.5">
                  <Label>E-mail</Label>
                  <Input value={perfil.email} disabled />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="flex flex-col gap-1.5">
                    <Label htmlFor="dataNascimento">Data de nascimento</Label>
                    <Input id="dataNascimento" type="date" {...register('dataNascimento')} />
                  </div>
                  <div className="flex flex-col gap-1.5">
                    <Label htmlFor="alturaCm">Altura (cm)</Label>
                    <Input id="alturaCm" type="number" step="0.1" {...register('alturaCm')} />
                    {errors.alturaCm && <p className="text-xs text-destructive">{errors.alturaCm.message}</p>}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="flex flex-col gap-1.5">
                    <Label htmlFor="sexo">Sexo</Label>
                    <Controller
                      control={control}
                      name="sexo"
                      render={({ field }) => (
                        <Select value={field.value} onValueChange={field.onChange}>
                          <SelectTrigger id="sexo">
                            <SelectValue placeholder="Selecione..." />
                          </SelectTrigger>
                          <SelectContent>
                            {Object.entries(LABELS_SEXO).map(([valor, label]) => (
                              <SelectItem key={valor} value={valor}>
                                {label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      )}
                    />
                  </div>
                  <div className="flex flex-col gap-1.5">
                    <Label htmlFor="nivelExperiencia">Nível de experiência</Label>
                    <Controller
                      control={control}
                      name="nivelExperiencia"
                      render={({ field }) => (
                        <Select value={field.value} onValueChange={field.onChange}>
                          <SelectTrigger id="nivelExperiencia">
                            <SelectValue placeholder="Selecione..." />
                          </SelectTrigger>
                          <SelectContent>
                            {Object.entries(LABELS_NIVEL).map(([valor, label]) => (
                              <SelectItem key={valor} value={valor}>
                                {label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      )}
                    />
                  </div>
                </div>

                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="bio">Bio</Label>
                  <Textarea id="bio" {...register('bio')} />
                  {errors.bio && <p className="text-xs text-destructive">{errors.bio.message}</p>}
                </div>

                <div className="flex items-center gap-3">
                  <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? 'Salvando...' : 'Salvar alterações'}
                  </Button>
                  {salvo && <span className="text-sm text-muted-foreground">Perfil atualizado!</span>}
                </div>
              </form>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay: 0.1 }}>
          <ObjetivoCard objetivoAtivo={objetivoAtivo} onCriado={setObjetivoAtivo} />
        </motion.div>
      </div>
    </div>
  )
}
