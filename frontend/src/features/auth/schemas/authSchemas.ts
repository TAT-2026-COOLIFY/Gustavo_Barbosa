import { z } from 'zod'

const senhaSchema = z
  .string()
  .min(8, 'Senha deve ter ao menos 8 caracteres')
  .max(72, 'Senha deve ter no máximo 72 caracteres')
  .regex(/(?=.*[A-Za-z])(?=.*\d)/, 'Senha deve conter ao menos uma letra e um número')

export const cadastroSchema = z
  .object({
    nome: z.string().min(1, 'Nome é obrigatório').max(120),
    email: z.string().min(1, 'E-mail é obrigatório').email('E-mail inválido').max(180),
    senha: senhaSchema,
    confirmarSenha: z.string().min(1, 'Confirme sua senha'),
  })
  .refine((data) => data.senha === data.confirmarSenha, {
    message: 'As senhas não coincidem',
    path: ['confirmarSenha'],
  })

export type CadastroFormValues = z.infer<typeof cadastroSchema>

export const loginSchema = z.object({
  email: z.string().min(1, 'E-mail é obrigatório').email('E-mail inválido'),
  senha: z.string().min(1, 'Senha é obrigatória'),
  lembrar: z.boolean(),
})

export type LoginFormValues = z.infer<typeof loginSchema>

export const esqueciSenhaSchema = z.object({
  email: z.string().min(1, 'E-mail é obrigatório').email('E-mail inválido'),
})

export type EsqueciSenhaFormValues = z.infer<typeof esqueciSenhaSchema>

export const redefinirSenhaSchema = z
  .object({
    novaSenha: senhaSchema,
    confirmarSenha: z.string().min(1, 'Confirme sua senha'),
  })
  .refine((data) => data.novaSenha === data.confirmarSenha, {
    message: 'As senhas não coincidem',
    path: ['confirmarSenha'],
  })

export type RedefinirSenhaFormValues = z.infer<typeof redefinirSenhaSchema>
