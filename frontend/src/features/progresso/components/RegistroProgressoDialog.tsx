import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Plus, Pencil } from 'lucide-react'

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
import { Textarea } from '@/components/ui/textarea'
import { registrarProgresso, atualizarProgresso } from '@/features/progresso/api/progressoApi'
import { registroProgressoSchema, type RegistroProgressoFormValues } from '@/features/progresso/schemas/progressoSchemas'
import type { RegistroProgressoResponseDTO } from '@/types/progresso'

interface RegistroProgressoDialogProps {
  registro?: RegistroProgressoResponseDTO
  onSalvo: (registro: RegistroProgressoResponseDTO) => void
}

export function RegistroProgressoDialog({ registro, onSalvo }: RegistroProgressoDialogProps) {
  const editando = !!registro
  const [open, setOpen] = useState(false)

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<RegistroProgressoFormValues>({
    resolver: zodResolver(registroProgressoSchema),
    values: registro
      ? {
          dataRegistro: registro.dataRegistro,
          pesoKg: String(registro.pesoKg),
          percentualGordura: registro.percentualGordura ? String(registro.percentualGordura) : '',
          circunferenciaCinturaCm: registro.circunferenciaCinturaCm ? String(registro.circunferenciaCinturaCm) : '',
          circunferenciaQuadrilCm: registro.circunferenciaQuadrilCm ? String(registro.circunferenciaQuadrilCm) : '',
          circunferenciaBracoCm: registro.circunferenciaBracoCm ? String(registro.circunferenciaBracoCm) : '',
          circunferenciaCoxaCm: registro.circunferenciaCoxaCm ? String(registro.circunferenciaCoxaCm) : '',
          circunferenciaPeitoCm: registro.circunferenciaPeitoCm ? String(registro.circunferenciaPeitoCm) : '',
          observacoes: registro.observacoes ?? '',
        }
      : undefined,
  })

  async function onSubmit(values: RegistroProgressoFormValues) {
    const input = {
      dataRegistro: values.dataRegistro || null,
      pesoKg: Number(values.pesoKg),
      percentualGordura: values.percentualGordura ? Number(values.percentualGordura) : null,
      circunferenciaCinturaCm: values.circunferenciaCinturaCm ? Number(values.circunferenciaCinturaCm) : null,
      circunferenciaQuadrilCm: values.circunferenciaQuadrilCm ? Number(values.circunferenciaQuadrilCm) : null,
      circunferenciaBracoCm: values.circunferenciaBracoCm ? Number(values.circunferenciaBracoCm) : null,
      circunferenciaCoxaCm: values.circunferenciaCoxaCm ? Number(values.circunferenciaCoxaCm) : null,
      circunferenciaPeitoCm: values.circunferenciaPeitoCm ? Number(values.circunferenciaPeitoCm) : null,
      observacoes: values.observacoes || null,
    }

    const salvo = editando ? await atualizarProgresso(registro.id, input) : await registrarProgresso(input)
    onSalvo(salvo)
    if (!editando) reset({ pesoKg: '', dataRegistro: '' })
    setOpen(false)
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {editando ? (
          <Button variant="ghost" size="icon" aria-label="Editar registro">
            <Pencil className="size-4" />
          </Button>
        ) : (
          <Button>
            <Plus className="size-4" />
            Registrar hoje
          </Button>
        )}
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{editando ? 'Editar registro' : 'Novo registro de progresso'}</DialogTitle>
          <DialogDescription>
            {editando ? 'Atualize os dados desse dia.' : 'Peso é obrigatório; o resto é opcional.'}
          </DialogDescription>
        </DialogHeader>

        <form className="flex max-h-[70vh] flex-col gap-4 overflow-y-auto pr-1" onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="grid grid-cols-2 gap-4">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="dataRegistro">Data</Label>
              <Input id="dataRegistro" type="date" {...register('dataRegistro')} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="pesoKg">Peso (kg)</Label>
              <Input id="pesoKg" type="number" step="0.1" aria-invalid={!!errors.pesoKg} {...register('pesoKg')} />
              {errors.pesoKg && <p className="text-xs text-destructive">{errors.pesoKg.message}</p>}
            </div>
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="percentualGordura">% de gordura corporal</Label>
            <Input id="percentualGordura" type="number" step="0.1" {...register('percentualGordura')} />
            {errors.percentualGordura && <p className="text-xs text-destructive">{errors.percentualGordura.message}</p>}
          </div>

          <p className="text-xs font-medium text-muted-foreground uppercase">Circunferências (cm)</p>
          <div className="grid grid-cols-2 gap-4">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="circunferenciaCinturaCm">Cintura</Label>
              <Input id="circunferenciaCinturaCm" type="number" step="0.1" {...register('circunferenciaCinturaCm')} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="circunferenciaQuadrilCm">Quadril</Label>
              <Input id="circunferenciaQuadrilCm" type="number" step="0.1" {...register('circunferenciaQuadrilCm')} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="circunferenciaBracoCm">Braço</Label>
              <Input id="circunferenciaBracoCm" type="number" step="0.1" {...register('circunferenciaBracoCm')} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="circunferenciaCoxaCm">Coxa</Label>
              <Input id="circunferenciaCoxaCm" type="number" step="0.1" {...register('circunferenciaCoxaCm')} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="circunferenciaPeitoCm">Peito</Label>
              <Input id="circunferenciaPeitoCm" type="number" step="0.1" {...register('circunferenciaPeitoCm')} />
            </div>
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="observacoes">Observações</Label>
            <Textarea id="observacoes" {...register('observacoes')} />
            {errors.observacoes && <p className="text-xs text-destructive">{errors.observacoes.message}</p>}
          </div>

          <DialogFooter>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Salvando...' : 'Salvar registro'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
