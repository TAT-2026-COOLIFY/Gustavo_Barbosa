import { useState } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Target } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { criarObjetivo } from '@/features/perfil/api/perfilApi'
import { objetivoSchema, type ObjetivoFormValues } from '@/features/perfil/schemas/perfilSchemas'
import type { ObjetivoResponseDTO } from '@/types/perfil'

const LABELS_TIPO: Record<string, string> = {
  EMAGRECIMENTO: 'Emagrecimento',
  HIPERTROFIA: 'Hipertrofia',
  MANUTENCAO: 'Manutenção',
  CONDICIONAMENTO: 'Condicionamento',
  SAUDE: 'Saúde',
}

interface ObjetivoCardProps {
  objetivoAtivo: ObjetivoResponseDTO | null
  onCriado: (objetivo: ObjetivoResponseDTO) => void
}

export function ObjetivoCard({ objetivoAtivo, onCriado }: ObjetivoCardProps) {
  const [open, setOpen] = useState(false)

  const {
    control,
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<ObjetivoFormValues>({ resolver: zodResolver(objetivoSchema) })

  async function onSubmit(values: ObjetivoFormValues) {
    const objetivo = await criarObjetivo({
      tipo: values.tipo,
      pesoMetaKg: values.pesoMetaKg ? Number(values.pesoMetaKg) : null,
      dataAlvo: values.dataAlvo || null,
    })
    onCriado(objetivo)
    reset()
    setOpen(false)
  }

  return (
    <Card className="glass border-0">
      <CardHeader>
        <CardTitle className="flex items-center gap-2 text-base">
          <Target className="size-4" />
          Objetivo
        </CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        {objetivoAtivo ? (
          <div className="text-sm">
            <p className="font-medium">{LABELS_TIPO[objetivoAtivo.tipo]}</p>
            {objetivoAtivo.pesoMetaKg && (
              <p className="text-muted-foreground">Meta: {objetivoAtivo.pesoMetaKg}kg</p>
            )}
            {objetivoAtivo.dataAlvo && (
              <p className="text-muted-foreground">Até {new Date(objetivoAtivo.dataAlvo).toLocaleDateString('pt-BR')}</p>
            )}
          </div>
        ) : (
          <p className="text-sm text-muted-foreground">Nenhum objetivo definido ainda.</p>
        )}

        <Dialog open={open} onOpenChange={setOpen}>
          <DialogTrigger asChild>
            <Button variant="outline" size="sm">
              {objetivoAtivo ? 'Definir novo objetivo' : 'Definir objetivo'}
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Novo objetivo</DialogTitle>
              <DialogDescription>Isso substitui o objetivo ativo atual.</DialogDescription>
            </DialogHeader>

            <form className="flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)} noValidate>
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="tipo">Tipo</Label>
                <Controller
                  control={control}
                  name="tipo"
                  render={({ field }) => (
                    <Select value={field.value} onValueChange={field.onChange}>
                      <SelectTrigger id="tipo" aria-invalid={!!errors.tipo}>
                        <SelectValue placeholder="Selecione..." />
                      </SelectTrigger>
                      <SelectContent>
                        {Object.entries(LABELS_TIPO).map(([valor, label]) => (
                          <SelectItem key={valor} value={valor}>
                            {label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  )}
                />
                {errors.tipo && <p className="text-xs text-destructive">{errors.tipo.message}</p>}
              </div>

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="pesoMetaKg">Peso meta (kg)</Label>
                <Input id="pesoMetaKg" type="number" step="0.1" {...register('pesoMetaKg')} />
                {errors.pesoMetaKg && <p className="text-xs text-destructive">{errors.pesoMetaKg.message}</p>}
              </div>

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="dataAlvo">Data alvo</Label>
                <Input id="dataAlvo" type="date" {...register('dataAlvo')} />
              </div>

              <DialogFooter>
                <Button type="submit" disabled={isSubmitting}>
                  {isSubmitting ? 'Salvando...' : 'Salvar objetivo'}
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>
      </CardContent>
    </Card>
  )
}
