import { useEffect, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Plus, Search } from 'lucide-react'

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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { buscarAlimentos, criarAlimento } from '@/features/dieta/api/alimentoApi'
import { alimentoSchema, type AlimentoFormValues } from '@/features/dieta/schemas/dietaSchemas'
import type { AlimentoResponseDTO, CategoriaAlimento } from '@/types/dieta'

const LABELS_CATEGORIA: Record<CategoriaAlimento, string> = {
  CEREAIS: 'Cereais',
  PROTEINAS: 'Proteínas',
  LATICINIOS: 'Laticínios',
  FRUTAS: 'Frutas',
  VEGETAIS: 'Vegetais',
  GORDURAS: 'Gorduras',
  BEBIDAS: 'Bebidas',
  DOCES: 'Doces',
  OUTROS: 'Outros',
}

interface AlimentoSearchDialogProps {
  onSelecionar: (alimento: AlimentoResponseDTO) => void
}

export function AlimentoSearchDialog({ onSelecionar }: AlimentoSearchDialogProps) {
  const [open, setOpen] = useState(false)
  const [criandoNovo, setCriandoNovo] = useState(false)
  const [q, setQ] = useState('')
  const [resultados, setResultados] = useState<AlimentoResponseDTO[]>([])

  const {
    control,
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<AlimentoFormValues>({ resolver: zodResolver(alimentoSchema) })

  useEffect(() => {
    if (!open || criandoNovo) return
    const timeout = setTimeout(() => {
      buscarAlimentos(q).then(setResultados)
    }, 250)
    return () => clearTimeout(timeout)
  }, [q, open, criandoNovo])

  function selecionar(alimento: AlimentoResponseDTO) {
    onSelecionar(alimento)
    setOpen(false)
    setQ('')
  }

  async function onSubmitNovoAlimento(values: AlimentoFormValues) {
    const alimento = await criarAlimento({
      nome: values.nome,
      marca: values.marca || null,
      categoria: values.categoria as CategoriaAlimento,
      caloriasPor100g: Number(values.caloriasPor100g),
      proteinaPor100g: Number(values.proteinaPor100g),
      carboidratoPor100g: Number(values.carboidratoPor100g),
      gorduraPor100g: Number(values.gorduraPor100g),
      fibraPor100g: values.fibraPor100g ? Number(values.fibraPor100g) : null,
    })
    reset()
    setCriandoNovo(false)
    selecionar(alimento)
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button type="button" size="sm" variant="outline">
          <Plus />
          Adicionar alimento
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{criandoNovo ? 'Novo alimento' : 'Buscar alimento'}</DialogTitle>
          <DialogDescription>
            {criandoNovo
              ? 'Valores nutricionais por 100g/100ml.'
              : 'Busque no catálogo ou cadastre um alimento personalizado.'}
          </DialogDescription>
        </DialogHeader>

        {criandoNovo ? (
          <form className="flex flex-col gap-3" onSubmit={handleSubmit(onSubmitNovoAlimento)} noValidate>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="alimento-nome">Nome</Label>
              <Input id="alimento-nome" aria-invalid={!!errors.nome} {...register('nome')} />
              {errors.nome && <p className="text-xs text-destructive">{errors.nome.message}</p>}
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="alimento-categoria">Categoria</Label>
              <Controller
                control={control}
                name="categoria"
                render={({ field }) => (
                  <Select value={field.value} onValueChange={field.onChange}>
                    <SelectTrigger id="alimento-categoria" aria-invalid={!!errors.categoria}>
                      <SelectValue placeholder="Selecione..." />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.entries(LABELS_CATEGORIA).map(([valor, label]) => (
                        <SelectItem key={valor} value={valor}>
                          {label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                )}
              />
              {errors.categoria && <p className="text-xs text-destructive">{errors.categoria.message}</p>}
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label className="text-xs">Calorias/100g</Label>
                <Input className="mt-1" {...register('caloriasPor100g')} />
              </div>
              <div>
                <Label className="text-xs">Proteína/100g</Label>
                <Input className="mt-1" {...register('proteinaPor100g')} />
              </div>
              <div>
                <Label className="text-xs">Carboidrato/100g</Label>
                <Input className="mt-1" {...register('carboidratoPor100g')} />
              </div>
              <div>
                <Label className="text-xs">Gordura/100g</Label>
                <Input className="mt-1" {...register('gorduraPor100g')} />
              </div>
            </div>

            <DialogFooter className="gap-2 sm:justify-between">
              <Button type="button" variant="ghost" size="sm" onClick={() => setCriandoNovo(false)}>
                Voltar para busca
              </Button>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Salvando...' : 'Salvar e adicionar'}
              </Button>
            </DialogFooter>
          </form>
        ) : (
          <div className="flex flex-col gap-3">
            <div className="relative">
              <Search className="absolute top-1/2 left-3 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                className="pl-9"
                placeholder="Buscar por nome..."
                value={q}
                onChange={(e) => setQ(e.target.value)}
                autoFocus
              />
            </div>

            <div className="flex max-h-72 flex-col gap-1 overflow-y-auto">
              {resultados.length === 0 ? (
                <p className="py-4 text-center text-sm text-muted-foreground">Nenhum alimento encontrado.</p>
              ) : (
                resultados.map((alimento) => (
                  <button
                    key={alimento.id}
                    type="button"
                    onClick={() => selecionar(alimento)}
                    className="flex flex-col items-start gap-0.5 rounded-lg px-3 py-2 text-left transition-colors hover:bg-accent"
                  >
                    <span className="text-sm font-medium">{alimento.nome}</span>
                    <span className="text-xs text-muted-foreground">
                      {alimento.caloriasPor100g} kcal · {alimento.proteinaPor100g}g P · {alimento.carboidratoPor100g}g C ·{' '}
                      {alimento.gorduraPor100g}g G (por 100g)
                    </span>
                  </button>
                ))
              )}
            </div>

            <Button type="button" variant="outline" size="sm" onClick={() => setCriandoNovo(true)}>
              <Plus />
              Não encontrei, cadastrar novo
            </Button>
          </div>
        )}
      </DialogContent>
    </Dialog>
  )
}
