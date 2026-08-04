import { useFieldArray, useFormContext, type Control } from 'react-hook-form'
import { Trash2, X } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { AlimentoSearchDialog } from '@/features/dieta/components/AlimentoSearchDialog'
import type { DietaFormValues } from '@/features/dieta/schemas/dietaSchemas'

interface RefeicaoFormItemProps {
  control: Control<DietaFormValues>
  index: number
  onRemoverRefeicao: () => void
}

export function RefeicaoFormItem({ control, index, onRemoverRefeicao }: RefeicaoFormItemProps) {
  const { register, formState } = useFormContext<DietaFormValues>()
  const { fields, append, remove } = useFieldArray({ control, name: `refeicoes.${index}.alimentos` })

  const errosAlimentos = formState.errors.refeicoes?.[index]?.alimentos
  const erroLista = errosAlimentos && !Array.isArray(errosAlimentos) ? errosAlimentos.message : undefined

  return (
    <div className="glass flex flex-col gap-3 rounded-xl p-4">
      <div className="flex items-start gap-2">
        <div className="flex-1">
          <Label className="text-xs">Nome da refeição</Label>
          <Input className="mt-1" placeholder="Ex.: Café da manhã" {...register(`refeicoes.${index}.nome`)} />
        </div>
        <div className="w-28">
          <Label className="text-xs">Horário</Label>
          <Input className="mt-1" type="time" {...register(`refeicoes.${index}.horarioSugerido`)} />
        </div>
        <Button
          type="button"
          variant="ghost"
          size="icon"
          className="mt-6"
          onClick={onRemoverRefeicao}
          aria-label="Remover refeição"
        >
          <Trash2 />
        </Button>
      </div>

      {fields.length > 0 && (
        <div className="flex flex-col gap-2">
          {fields.map((field, alimentoIndex) => (
            <div key={field.id} className="flex items-center gap-2 rounded-lg bg-background/40 px-3 py-2">
              <span className="flex-1 truncate text-sm">{field.alimentoNome || 'Alimento'}</span>
              <Input
                className="w-20"
                placeholder="g"
                {...register(`refeicoes.${index}.alimentos.${alimentoIndex}.quantidadeGramas`)}
              />
              <Button
                type="button"
                variant="ghost"
                size="icon"
                onClick={() => remove(alimentoIndex)}
                aria-label="Remover alimento"
              >
                <X className="size-4" />
              </Button>
            </div>
          ))}
        </div>
      )}
      {erroLista && <p className="text-xs text-destructive">{erroLista}</p>}

      <AlimentoSearchDialog
        onSelecionar={(alimento) =>
          append({ alimentoId: String(alimento.id), alimentoNome: alimento.nome, quantidadeGramas: '100' })
        }
      />
    </div>
  )
}
