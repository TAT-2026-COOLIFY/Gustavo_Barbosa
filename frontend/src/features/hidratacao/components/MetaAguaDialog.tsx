import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Pencil } from 'lucide-react'

import { Button } from '@/components/ui/button'
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
import { atualizarMeta } from '@/features/hidratacao/api/hidratacaoApi'
import { metaHidratacaoSchema, type MetaHidratacaoFormValues } from '@/features/hidratacao/schemas/hidratacaoSchemas'
import type { MetaHidratacaoResponseDTO } from '@/types/hidratacao'

interface MetaAguaDialogProps {
  metaMl: number
  onAtualizada: (meta: MetaHidratacaoResponseDTO) => void
}

export function MetaAguaDialog({ metaMl, onAtualizada }: MetaAguaDialogProps) {
  const [open, setOpen] = useState(false)

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<MetaHidratacaoFormValues>({
    resolver: zodResolver(metaHidratacaoSchema),
    values: { metaMl: String(metaMl) },
  })

  async function onSubmit(values: MetaHidratacaoFormValues) {
    const atualizada = await atualizarMeta(Number(values.metaMl))
    onAtualizada(atualizada)
    setOpen(false)
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        setOpen(next)
        if (next) reset({ metaMl: String(metaMl) })
      }}
    >
      <DialogTrigger asChild>
        <Button variant="outline" size="sm">
          <Pencil className="size-3.5" />
          Editar meta
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Meta diária de água</DialogTitle>
          <DialogDescription>Defina quantos ml você pretende beber por dia.</DialogDescription>
        </DialogHeader>

        <form className="flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="metaMl">Meta (ml)</Label>
            <Input id="metaMl" type="number" step="50" aria-invalid={!!errors.metaMl} {...register('metaMl')} />
            {errors.metaMl && <p className="text-xs text-destructive">{errors.metaMl.message}</p>}
          </div>

          <DialogFooter>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Salvando...' : 'Salvar meta'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
