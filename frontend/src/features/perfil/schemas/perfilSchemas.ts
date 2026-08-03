import { z } from 'zod'

export const atualizarPerfilSchema = z.object({
  nome: z.string().min(1, 'Nome é obrigatório').max(120),
  dataNascimento: z.string().optional().or(z.literal('')),
  sexo: z.enum(['MASCULINO', 'FEMININO', 'OUTRO']).optional(),
  alturaCm: z
    .string()
    .optional()
    .refine((v) => !v || (Number(v) >= 50 && Number(v) <= 260), 'Altura deve estar entre 50 e 260cm'),
  nivelExperiencia: z.enum(['INICIANTE', 'INTERMEDIARIO', 'AVANCADO']).optional(),
  bio: z.string().max(500, 'Bio deve ter no máximo 500 caracteres').optional().or(z.literal('')),
})

export type AtualizarPerfilFormValues = z.infer<typeof atualizarPerfilSchema>

export const objetivoSchema = z.object({
  tipo: z.enum(['EMAGRECIMENTO', 'HIPERTROFIA', 'MANUTENCAO', 'CONDICIONAMENTO', 'SAUDE'], {
    message: 'Selecione um tipo de objetivo',
  }),
  pesoMetaKg: z
    .string()
    .optional()
    .refine((v) => !v || (Number(v) >= 20 && Number(v) <= 400), 'Peso meta deve estar entre 20 e 400kg'),
  dataAlvo: z.string().optional().or(z.literal('')),
})

export type ObjetivoFormValues = z.infer<typeof objetivoSchema>
