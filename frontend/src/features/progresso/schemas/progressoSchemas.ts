import { z } from 'zod'

function numeroOpcionalEntre(min: number, max: number, mensagem: string) {
  return z
    .string()
    .optional()
    .refine((v) => !v || (Number(v) >= min && Number(v) <= max), mensagem)
}

export const registroProgressoSchema = z.object({
  dataRegistro: z.string().optional().or(z.literal('')),
  pesoKg: z
    .string()
    .min(1, 'Peso é obrigatório')
    .refine((v) => Number(v) >= 20 && Number(v) <= 400, 'Peso deve estar entre 20 e 400kg'),
  percentualGordura: numeroOpcionalEntre(0, 75, '% de gordura deve estar entre 0 e 75'),
  circunferenciaCinturaCm: numeroOpcionalEntre(30, 300, 'Circunferência deve estar entre 30 e 300cm'),
  circunferenciaQuadrilCm: numeroOpcionalEntre(30, 300, 'Circunferência deve estar entre 30 e 300cm'),
  circunferenciaBracoCm: numeroOpcionalEntre(10, 100, 'Circunferência deve estar entre 10 e 100cm'),
  circunferenciaCoxaCm: numeroOpcionalEntre(10, 150, 'Circunferência deve estar entre 10 e 150cm'),
  circunferenciaPeitoCm: numeroOpcionalEntre(30, 300, 'Circunferência deve estar entre 30 e 300cm'),
  observacoes: z.string().max(500, 'Observações devem ter no máximo 500 caracteres').optional().or(z.literal('')),
})

export type RegistroProgressoFormValues = z.infer<typeof registroProgressoSchema>
