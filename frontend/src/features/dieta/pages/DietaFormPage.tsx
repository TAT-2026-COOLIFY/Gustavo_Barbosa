import { useEffect, useState } from 'react'
import { Controller, FormProvider, useFieldArray, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, Plus, Sparkles } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Checkbox } from '@/components/ui/checkbox'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { RefeicaoFormItem } from '@/features/dieta/components/RefeicaoFormItem'
import { atualizarDieta, calcularMetas, criarDieta, obterDieta } from '@/features/dieta/api/dietaApi'
import { getUltimoProgresso } from '@/features/progresso/api/progressoApi'
import { dietaSchema, type DietaFormValues } from '@/features/dieta/schemas/dietaSchemas'
import type { MetaNutricionalCalculadaDTO, NivelAtividade } from '@/types/dieta'
import type { TipoObjetivo } from '@/types/perfil'

const LABELS_OBJETIVO: Record<TipoObjetivo, string> = {
  EMAGRECIMENTO: 'Emagrecimento',
  HIPERTROFIA: 'Hipertrofia',
  MANUTENCAO: 'Manutenção',
  CONDICIONAMENTO: 'Condicionamento',
  SAUDE: 'Saúde',
}

const LABELS_NIVEL_ATIVIDADE: Record<NivelAtividade, string> = {
  SEDENTARIO: 'Sedentário (pouco ou nenhum exercício)',
  LEVE: 'Leve (1 a 3x por semana)',
  MODERADO: 'Moderado (3 a 5x por semana)',
  INTENSO: 'Intenso (6 a 7x por semana)',
  MUITO_INTENSO: 'Muito intenso (2x ao dia ou trabalho físico)',
}

export function DietaFormPage() {
  const { id } = useParams()
  const editando = !!id
  const navigate = useNavigate()
  const [preview, setPreview] = useState<MetaNutricionalCalculadaDTO | null>(null)
  const [calculando, setCalculando] = useState(false)

  const form = useForm<DietaFormValues>({
    resolver: zodResolver(dietaSchema),
    defaultValues: { nome: '', calculoAutomatico: true, refeicoes: [] },
  })
  const {
    control,
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors, isSubmitting },
  } = form

  const { fields, append, remove } = useFieldArray({ control, name: 'refeicoes' })
  const calculoAutomatico = watch('calculoAutomatico')

  useEffect(() => {
    if (!editando) return
    obterDieta(Number(id)).then((dieta) => {
      reset({
        nome: dieta.nome,
        calculoAutomatico: dieta.calculoAutomatico,
        pesoReferenciaKg: dieta.pesoReferenciaKg ? String(dieta.pesoReferenciaKg) : '',
        nivelAtividade: dieta.nivelAtividade ?? undefined,
        tipoObjetivoReferencia: dieta.tipoObjetivoReferencia ?? undefined,
        metaCaloriasKcal: String(dieta.metaCaloriasKcal),
        metaProteinaG: String(dieta.metaProteinaG),
        metaCarboidratoG: String(dieta.metaCarboidratoG),
        metaGorduraG: String(dieta.metaGorduraG),
        metaAguaMl: String(dieta.metaAguaMl),
        refeicoes: dieta.refeicoes.map((refeicao) => ({
          nome: refeicao.nome,
          horarioSugerido: refeicao.horarioSugerido ?? '',
          alimentos: refeicao.alimentos.map((item) => ({
            alimentoId: String(item.alimentoId),
            alimentoNome: item.alimentoNome,
            quantidadeGramas: String(item.quantidadeGramas),
          })),
        })),
      })
    })
  }, [editando, id, reset])

  // Sugestão leve, sem acoplamento rígido: se o módulo de Progresso já tiver um peso registrado,
  // pré-preenche o campo (o usuário pode ajustar livremente). Se não houver registro, ignora.
  useEffect(() => {
    if (editando) return
    getUltimoProgresso()
      .then((ultimo) => {
        if (!form.getValues('pesoReferenciaKg')) {
          form.setValue('pesoReferenciaKg', String(ultimo.pesoKg))
        }
      })
      .catch(() => {})
  }, [editando, form])

  async function handleCalcularPreview() {
    const peso = form.getValues('pesoReferenciaKg')
    const nivel = form.getValues('nivelAtividade') as NivelAtividade | undefined
    const objetivo = form.getValues('tipoObjetivoReferencia') as TipoObjetivo | undefined
    if (!peso || !nivel || !objetivo) return

    setCalculando(true)
    try {
      const resultado = await calcularMetas({ pesoReferenciaKg: Number(peso), nivelAtividade: nivel, tipoObjetivo: objetivo })
      setPreview(resultado)
    } finally {
      setCalculando(false)
    }
  }

  async function onSubmit(values: DietaFormValues) {
    const payload = {
      nome: values.nome,
      calculoAutomatico: values.calculoAutomatico,
      pesoReferenciaKg: values.pesoReferenciaKg ? Number(values.pesoReferenciaKg) : null,
      nivelAtividade: (values.nivelAtividade as NivelAtividade) || null,
      tipoObjetivoReferencia: (values.tipoObjetivoReferencia as TipoObjetivo) || null,
      metaCaloriasKcal: values.metaCaloriasKcal ? Number(values.metaCaloriasKcal) : null,
      metaProteinaG: values.metaProteinaG ? Number(values.metaProteinaG) : null,
      metaCarboidratoG: values.metaCarboidratoG ? Number(values.metaCarboidratoG) : null,
      metaGorduraG: values.metaGorduraG ? Number(values.metaGorduraG) : null,
      metaAguaMl: values.metaAguaMl ? Number(values.metaAguaMl) : null,
      refeicoes: values.refeicoes.map((refeicao, index) => ({
        nome: refeicao.nome,
        ordem: index + 1,
        horarioSugerido: refeicao.horarioSugerido || null,
        alimentos: refeicao.alimentos.map((item, itemIndex) => ({
          alimentoId: Number(item.alimentoId),
          ordem: itemIndex + 1,
          quantidadeGramas: Number(item.quantidadeGramas),
        })),
      })),
    }

    if (editando) {
      await atualizarDieta(Number(id), payload)
    } else {
      await criarDieta(payload)
    }
    navigate('/dietas')
  }

  return (
    <div className="relative min-h-svh bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex max-w-2xl flex-col gap-6">
        <div className="flex items-center justify-between">
          <Button asChild variant="glass" size="icon">
            <Link to="/dietas" aria-label="Voltar">
              <ArrowLeft />
            </Link>
          </Button>
          <ThemeToggle />
        </div>

        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <Card className="glass border-0">
            <CardHeader>
              <CardTitle>{editando ? 'Editar dieta' : 'Nova dieta'}</CardTitle>
            </CardHeader>
            <CardContent>
              <FormProvider {...form}>
                <form className="flex flex-col gap-6" onSubmit={handleSubmit(onSubmit)} noValidate>
                  <div className="flex flex-col gap-1.5">
                    <Label htmlFor="nome">Nome da dieta</Label>
                    <Input id="nome" aria-invalid={!!errors.nome} {...register('nome')} />
                    {errors.nome && <p className="text-xs text-destructive">{errors.nome.message}</p>}
                  </div>

                  <div className="flex items-center gap-2">
                    <Controller
                      control={control}
                      name="calculoAutomatico"
                      render={({ field }) => (
                        <Checkbox id="calculoAutomatico" checked={field.value} onCheckedChange={field.onChange} />
                      )}
                    />
                    <Label htmlFor="calculoAutomatico" className="cursor-pointer">
                      Calcular metas nutricionais automaticamente
                    </Label>
                  </div>

                  {calculoAutomatico ? (
                    <div className="glass flex flex-col gap-3 rounded-xl p-4">
                      <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
                        <div>
                          <Label className="text-xs">Peso de referência (kg)</Label>
                          <Input className="mt-1" {...register('pesoReferenciaKg')} />
                          {errors.pesoReferenciaKg && (
                            <p className="text-xs text-destructive">{errors.pesoReferenciaKg.message}</p>
                          )}
                        </div>
                        <div>
                          <Label className="text-xs">Nível de atividade</Label>
                          <Controller
                            control={control}
                            name="nivelAtividade"
                            render={({ field }) => (
                              <Select value={field.value} onValueChange={field.onChange}>
                                <SelectTrigger className="mt-1">
                                  <SelectValue placeholder="Selecione..." />
                                </SelectTrigger>
                                <SelectContent>
                                  {Object.entries(LABELS_NIVEL_ATIVIDADE).map(([valor, label]) => (
                                    <SelectItem key={valor} value={valor}>
                                      {label}
                                    </SelectItem>
                                  ))}
                                </SelectContent>
                              </Select>
                            )}
                          />
                        </div>
                        <div>
                          <Label className="text-xs">Objetivo</Label>
                          <Controller
                            control={control}
                            name="tipoObjetivoReferencia"
                            render={({ field }) => (
                              <Select value={field.value} onValueChange={field.onChange}>
                                <SelectTrigger className="mt-1">
                                  <SelectValue placeholder="Selecione..." />
                                </SelectTrigger>
                                <SelectContent>
                                  {Object.entries(LABELS_OBJETIVO).map(([valor, label]) => (
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

                      <Button type="button" variant="outline" size="sm" onClick={handleCalcularPreview} disabled={calculando}>
                        <Sparkles />
                        {calculando ? 'Calculando...' : 'Calcular metas'}
                      </Button>

                      {preview && (
                        <div className="grid grid-cols-2 gap-2 text-sm sm:grid-cols-3">
                          <span>Calorias: {preview.metaCaloriasKcal} kcal</span>
                          <span>Proteína: {preview.metaProteinaG}g</span>
                          <span>Carboidrato: {preview.metaCarboidratoG}g</span>
                          <span>Gordura: {preview.metaGorduraG}g</span>
                          <span>Água: {preview.metaAguaMl}ml</span>
                        </div>
                      )}
                    </div>
                  ) : (
                    <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
                      <div>
                        <Label className="text-xs">Calorias (kcal)</Label>
                        <Input className="mt-1" {...register('metaCaloriasKcal')} />
                      </div>
                      <div>
                        <Label className="text-xs">Proteína (g)</Label>
                        <Input className="mt-1" {...register('metaProteinaG')} />
                      </div>
                      <div>
                        <Label className="text-xs">Carboidrato (g)</Label>
                        <Input className="mt-1" {...register('metaCarboidratoG')} />
                      </div>
                      <div>
                        <Label className="text-xs">Gordura (g)</Label>
                        <Input className="mt-1" {...register('metaGorduraG')} />
                      </div>
                      <div>
                        <Label className="text-xs">Água (ml)</Label>
                        <Input className="mt-1" {...register('metaAguaMl')} />
                      </div>
                    </div>
                  )}

                  <div className="flex flex-col gap-3">
                    <div className="flex items-center justify-between">
                      <Label>Refeições</Label>
                      <Button
                        type="button"
                        size="sm"
                        variant="outline"
                        onClick={() => append({ nome: '', horarioSugerido: '', alimentos: [] })}
                      >
                        <Plus />
                        Adicionar refeição
                      </Button>
                    </div>
                    {errors.refeicoes && !Array.isArray(errors.refeicoes) && (
                      <p className="text-xs text-destructive">{errors.refeicoes.message}</p>
                    )}

                    {fields.map((field, index) => (
                      <RefeicaoFormItem key={field.id} control={control} index={index} onRemoverRefeicao={() => remove(index)} />
                    ))}
                  </div>

                  <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? 'Salvando...' : 'Salvar dieta'}
                  </Button>
                </form>
              </FormProvider>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  )
}
