import { Plus, X } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { CopoAguaAnimado } from '@/features/hidratacao/components/CopoAguaAnimado'
import { MetaAguaDialog } from '@/features/hidratacao/components/MetaAguaDialog'
import type { MetaHidratacaoResponseDTO, ResumoDiaHidratacaoResponseDTO } from '@/types/hidratacao'

const QUANTIDADES_RAPIDAS = [200, 250, 500]

interface RegistroAguaCardProps {
  resumo: ResumoDiaHidratacaoResponseDTO
  onAdicionar: (quantidadeMl: number) => void
  onRemover: (registroId: number) => void
  onMetaAtualizada: (meta: MetaHidratacaoResponseDTO) => void
  adicionando: boolean
  pulseKey: number
}

export function RegistroAguaCard({
  resumo,
  onAdicionar,
  onRemover,
  onMetaAtualizada,
  adicionando,
  pulseKey,
}: RegistroAguaCardProps) {
  return (
    <div className="flex flex-col gap-5">
      <div className="flex justify-end">
        <MetaAguaDialog metaMl={resumo.metaMl} onAtualizada={onMetaAtualizada} />
      </div>

      <CopoAguaAnimado
        percentual={resumo.percentual}
        totalMl={resumo.totalMl}
        metaMl={resumo.metaMl}
        pulseKey={pulseKey}
      />

      <div className="flex flex-wrap justify-center gap-2">
        {QUANTIDADES_RAPIDAS.map((quantidade) => (
          <Button key={quantidade} onClick={() => onAdicionar(quantidade)} disabled={adicionando}>
            <Plus className="size-4" />+{quantidade}ml
          </Button>
        ))}
      </div>

      {resumo.registros.length > 0 && (
        <div className="flex flex-col gap-1.5">
          <p className="text-xs font-medium text-muted-foreground uppercase">Registros de hoje</p>
          <div className="flex flex-col gap-1">
            {resumo.registros.map((registro) => (
              <div
                key={registro.id}
                className="flex items-center justify-between rounded-lg bg-muted/40 px-3 py-1.5 text-sm"
              >
                <span>
                  {registro.quantidadeMl}ml ·{' '}
                  {new Date(registro.registradoEm).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}
                </span>
                <button
                  type="button"
                  aria-label="Remover registro"
                  className="text-muted-foreground transition-colors hover:text-destructive"
                  onClick={() => onRemover(registro.id)}
                >
                  <X className="size-3.5" />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
