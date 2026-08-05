import { cn } from '@/lib/utils'
import { SugestaoDietaCard } from '@/features/ia/components/SugestaoDietaCard'
import { SugestaoTreinoCard } from '@/features/ia/components/SugestaoTreinoCard'
import type { AlimentoResponseDTO } from '@/types/dieta'
import type { MensagemComRascunhoDTO } from '@/types/ia'
import type { ExercicioResponseDTO } from '@/types/treino'

interface MensagemBubbleProps {
  mensagem: MensagemComRascunhoDTO
  exercicios: ExercicioResponseDTO[]
  alimentos: AlimentoResponseDTO[]
  onRascunhoDescartado: (rascunhoId: number) => void
}

export function MensagemBubble({ mensagem, exercicios, alimentos, onRascunhoDescartado }: MensagemBubbleProps) {
  const isUsuario = mensagem.papel === 'USUARIO'

  return (
    <div className={cn('flex flex-col', isUsuario ? 'items-end' : 'items-start')}>
      <div
        className={cn(
          'max-w-[80%] rounded-2xl px-4 py-2 text-sm whitespace-pre-wrap',
          isUsuario ? 'bg-primary text-primary-foreground' : 'glass',
        )}
      >
        {mensagem.conteudo}
      </div>

      {mensagem.rascunho?.tipo === 'TREINO' && (
        <SugestaoTreinoCard rascunho={mensagem.rascunho} exercicios={exercicios} onDescartado={onRascunhoDescartado} />
      )}
      {mensagem.rascunho?.tipo === 'DIETA' && (
        <SugestaoDietaCard rascunho={mensagem.rascunho} alimentos={alimentos} onDescartado={onRascunhoDescartado} />
      )}
    </div>
  )
}
