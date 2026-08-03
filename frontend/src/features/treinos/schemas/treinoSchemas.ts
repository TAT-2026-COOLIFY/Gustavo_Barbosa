import { z } from 'zod'

export const treinoExercicioSchema = z.object({
  exercicioId: z.string().min(1, 'Selecione um exercício'),
  seriesAlvo: z
    .string()
    .min(1, 'Obrigatório')
    .refine((v) => Number(v) >= 1 && Number(v) <= 20, 'Entre 1 e 20'),
  repeticoesAlvo: z
    .string()
    .min(1, 'Obrigatório')
    .refine((v) => Number(v) >= 1 && Number(v) <= 100, 'Entre 1 e 100'),
  cargaAlvoKg: z.string().optional(),
  descansoSegundos: z.string().optional(),
})

export const treinoSchema = z.object({
  nome: z.string().min(1, 'Nome é obrigatório').max(120),
  exercicios: z.array(treinoExercicioSchema).min(1, 'Adicione ao menos um exercício'),
})

export type TreinoFormValues = z.infer<typeof treinoSchema>
