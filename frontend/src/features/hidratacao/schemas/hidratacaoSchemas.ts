import { z } from 'zod'

export const metaHidratacaoSchema = z.object({
  metaMl: z
    .string()
    .min(1, 'Meta é obrigatória')
    .refine((v) => Number(v) >= 500 && Number(v) <= 10000, 'Meta deve estar entre 500ml e 10000ml'),
})

export type MetaHidratacaoFormValues = z.infer<typeof metaHidratacaoSchema>
