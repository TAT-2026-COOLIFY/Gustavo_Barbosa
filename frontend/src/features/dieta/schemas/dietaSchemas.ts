import { z } from 'zod'

export const refeicaoAlimentoSchema = z.object({
  alimentoId: z.string().min(1, 'Selecione um alimento'),
  alimentoNome: z.string().optional(),
  quantidadeGramas: z
    .string()
    .min(1, 'Obrigatório')
    .refine((v) => Number(v) > 0, 'Quantidade deve ser positiva'),
})

export const refeicaoSchema = z.object({
  nome: z.string().min(1, 'Nome é obrigatório').max(80),
  horarioSugerido: z.string().optional(),
  alimentos: z.array(refeicaoAlimentoSchema).min(1, 'Adicione ao menos um alimento'),
})

export const dietaSchema = z
  .object({
    nome: z.string().min(1, 'Nome é obrigatório').max(120),
    calculoAutomatico: z.boolean(),
    pesoReferenciaKg: z.string().optional(),
    nivelAtividade: z.string().optional(),
    tipoObjetivoReferencia: z.string().optional(),
    metaCaloriasKcal: z.string().optional(),
    metaProteinaG: z.string().optional(),
    metaCarboidratoG: z.string().optional(),
    metaGorduraG: z.string().optional(),
    metaAguaMl: z.string().optional(),
    refeicoes: z.array(refeicaoSchema).min(1, 'Adicione ao menos uma refeição'),
  })
  .superRefine((data, ctx) => {
    if (data.calculoAutomatico) {
      if (!data.pesoReferenciaKg) {
        ctx.addIssue({ code: 'custom', path: ['pesoReferenciaKg'], message: 'Peso é obrigatório' })
      }
      if (!data.nivelAtividade) {
        ctx.addIssue({ code: 'custom', path: ['nivelAtividade'], message: 'Nível de atividade é obrigatório' })
      }
      if (!data.tipoObjetivoReferencia) {
        ctx.addIssue({ code: 'custom', path: ['tipoObjetivoReferencia'], message: 'Objetivo é obrigatório' })
      }
    } else {
      const camposMeta = [
        'metaCaloriasKcal',
        'metaProteinaG',
        'metaCarboidratoG',
        'metaGorduraG',
        'metaAguaMl',
      ] as const
      camposMeta.forEach((campo) => {
        if (!data[campo]) {
          ctx.addIssue({ code: 'custom', path: [campo], message: 'Obrigatório' })
        }
      })
    }
  })

export type DietaFormValues = z.infer<typeof dietaSchema>

export const alimentoSchema = z.object({
  nome: z.string().min(1, 'Nome é obrigatório').max(150),
  marca: z.string().optional(),
  categoria: z.string().min(1, 'Selecione uma categoria'),
  caloriasPor100g: z.string().min(1, 'Obrigatório').refine((v) => Number(v) >= 0, 'Deve ser positivo'),
  proteinaPor100g: z.string().min(1, 'Obrigatório').refine((v) => Number(v) >= 0, 'Deve ser positivo'),
  carboidratoPor100g: z.string().min(1, 'Obrigatório').refine((v) => Number(v) >= 0, 'Deve ser positivo'),
  gorduraPor100g: z.string().min(1, 'Obrigatório').refine((v) => Number(v) >= 0, 'Deve ser positivo'),
  fibraPor100g: z.string().optional(),
})

export type AlimentoFormValues = z.infer<typeof alimentoSchema>
