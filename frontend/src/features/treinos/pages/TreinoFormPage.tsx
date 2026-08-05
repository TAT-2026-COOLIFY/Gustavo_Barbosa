import { useEffect, useState } from 'react'
import { useFieldArray, useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, Plus, Trash2 } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { confirmarRascunhoTreino } from '@/features/ia/api/iaApi'
import { criarTreino, listarExercicios, obterTreino, atualizarTreino } from '@/features/treinos/api/treinoApi'
import { treinoSchema, type TreinoFormValues } from '@/features/treinos/schemas/treinoSchemas'
import type { RascunhoTreinoDados } from '@/types/ia'
import type { ExercicioResponseDTO } from '@/types/treino'

export function TreinoFormPage() {
  const { id } = useParams()
  const editando = !!id
  const navigate = useNavigate()
  const location = useLocation()
  const rascunhoIa = location.state as { rascunhoIaId?: number; dados?: RascunhoTreinoDados } | null
  const [exercicios, setExercicios] = useState<ExercicioResponseDTO[]>([])

  const {
    control,
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<TreinoFormValues>({
    resolver: zodResolver(treinoSchema),
    defaultValues: { nome: '', exercicios: [] },
  })

  const { fields, append, remove } = useFieldArray({ control, name: 'exercicios' })

  useEffect(() => {
    listarExercicios().then(setExercicios)
  }, [])

  useEffect(() => {
    if (!editando) return
    obterTreino(Number(id)).then((treino) => {
      reset({
        nome: treino.nome,
        exercicios: treino.exercicios.map((te) => ({
          exercicioId: String(te.exercicio.id),
          seriesAlvo: String(te.seriesAlvo),
          repeticoesAlvo: String(te.repeticoesAlvo),
          cargaAlvoKg: te.cargaAlvoKg ? String(te.cargaAlvoKg) : '',
          descansoSegundos: te.descansoSegundos ? String(te.descansoSegundos) : '',
        })),
      })
    })
  }, [editando, id, reset])

  // Chegou a partir de uma sugestão do Assistente IA ("Editar e salvar"): pré-preenche com o rascunho,
  // que o usuário revisa e edita livremente antes de confirmar.
  useEffect(() => {
    if (editando || !rascunhoIa?.dados) return
    reset({
      nome: rascunhoIa.dados.nome,
      exercicios: rascunhoIa.dados.exercicios.map((ex) => ({
        exercicioId: String(ex.exercicioId),
        seriesAlvo: String(ex.seriesAlvo),
        repeticoesAlvo: String(ex.repeticoesAlvo),
        cargaAlvoKg: ex.cargaAlvoKg ? String(ex.cargaAlvoKg) : '',
        descansoSegundos: ex.descansoSegundos ? String(ex.descansoSegundos) : '',
      })),
    })
  }, [editando, rascunhoIa, reset])

  async function onSubmit(values: TreinoFormValues) {
    const payload = {
      nome: values.nome,
      exercicios: values.exercicios.map((ex, index) => ({
        exercicioId: Number(ex.exercicioId),
        ordem: index + 1,
        seriesAlvo: Number(ex.seriesAlvo),
        repeticoesAlvo: Number(ex.repeticoesAlvo),
        cargaAlvoKg: ex.cargaAlvoKg ? Number(ex.cargaAlvoKg) : null,
        descansoSegundos: ex.descansoSegundos ? Number(ex.descansoSegundos) : null,
      })),
    }

    if (editando) {
      await atualizarTreino(Number(id), payload)
    } else if (rascunhoIa?.rascunhoIaId) {
      await confirmarRascunhoTreino(rascunhoIa.rascunhoIaId, payload)
    } else {
      await criarTreino(payload)
    }
    navigate('/treinos')
  }

  return (
    <div className="relative min-h-svh bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex max-w-2xl flex-col gap-6">
        <div className="flex items-center justify-between">
          <Button asChild variant="glass" size="icon">
            <Link to="/treinos" aria-label="Voltar">
              <ArrowLeft />
            </Link>
          </Button>
          <ThemeToggle />
        </div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <Card className="glass border-0">
            <CardHeader>
              <CardTitle>
                {editando ? 'Editar treino' : rascunhoIa?.rascunhoIaId ? 'Revisar sugestão da IA' : 'Novo treino'}
              </CardTitle>
            </CardHeader>
            <CardContent>
              <form className="flex flex-col gap-6" onSubmit={handleSubmit(onSubmit)} noValidate>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="nome">Nome do treino</Label>
                  <Input id="nome" aria-invalid={!!errors.nome} {...register('nome')} />
                  {errors.nome && <p className="text-xs text-destructive">{errors.nome.message}</p>}
                </div>

                <div className="flex flex-col gap-3">
                  <div className="flex items-center justify-between">
                    <Label>Exercícios</Label>
                    <Button
                      type="button"
                      size="sm"
                      variant="outline"
                      onClick={() =>
                        append({ exercicioId: '', seriesAlvo: '3', repeticoesAlvo: '12', cargaAlvoKg: '', descansoSegundos: '60' })
                      }
                    >
                      <Plus />
                      Adicionar exercício
                    </Button>
                  </div>
                  {errors.exercicios && !Array.isArray(errors.exercicios) && (
                    <p className="text-xs text-destructive">{errors.exercicios.message}</p>
                  )}

                  {fields.map((field, index) => (
                    <div key={field.id} className="glass flex flex-col gap-3 rounded-xl p-4">
                      <div className="flex items-start gap-2">
                        <div className="flex-1">
                          <Label>Exercício</Label>
                          <Controller
                            control={control}
                            name={`exercicios.${index}.exercicioId`}
                            render={({ field: selectField }) => (
                              <Select value={selectField.value} onValueChange={selectField.onChange}>
                                <SelectTrigger className="mt-1.5">
                                  <SelectValue placeholder="Selecione..." />
                                </SelectTrigger>
                                <SelectContent>
                                  {exercicios.map((ex) => (
                                    <SelectItem key={ex.id} value={String(ex.id)}>
                                      {ex.nome}
                                    </SelectItem>
                                  ))}
                                </SelectContent>
                              </Select>
                            )}
                          />
                        </div>
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
                          className="mt-6"
                          onClick={() => remove(index)}
                          aria-label="Remover exercício"
                        >
                          <Trash2 />
                        </Button>
                      </div>

                      <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
                        <div>
                          <Label className="text-xs">Séries</Label>
                          <Input className="mt-1" {...register(`exercicios.${index}.seriesAlvo`)} />
                        </div>
                        <div>
                          <Label className="text-xs">Repetições</Label>
                          <Input className="mt-1" {...register(`exercicios.${index}.repeticoesAlvo`)} />
                        </div>
                        <div>
                          <Label className="text-xs">Carga (kg)</Label>
                          <Input className="mt-1" {...register(`exercicios.${index}.cargaAlvoKg`)} />
                        </div>
                        <div>
                          <Label className="text-xs">Descanso (s)</Label>
                          <Input className="mt-1" {...register(`exercicios.${index}.descansoSegundos`)} />
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                <Button type="submit" disabled={isSubmitting}>
                  {isSubmitting ? 'Salvando...' : 'Salvar treino'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  )
}
