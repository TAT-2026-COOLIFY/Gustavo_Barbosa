import { Plus, X } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Progress } from '@/components/ui/progress'
import { MetaAguaDialog } from '@/features/hidratacao/components/MetaAguaDialog'
import type { MetaHidratacaoResponseDTO, ResumoDiaHidratacaoResponseDTO } from '@/types/hidratacao'

const QUANTIDADES_RAPIDAS = [200, 250, 500]

interface RegistroAguaCardProps {
  resumo: ResumoDiaHidratacaoResponseDTO
  onAdicionar: (quantidadeMl: number) => void
  onRemover: (registroId: number) => void
  onMetaAtualizada: (meta: MetaHidratacaoResponseDTO) => void
  adicionando: boolean
}

export function RegistroAguaCard({ resumo, onAdicionar, onRemover, onMetaAtualizada, adicionando }: RegistroAguaCardProps) {
  return (
    <div className="flex flex-col gap-5">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-3xl font-semibold">
            {resumo.totalMl} <span className="text-base font-normal text-muted-foreground">/ {resumo.metaMl} ml</span>
          </p>
          <p className="text-sm text-muted-foreground">{resumo.percentual}% da meta de hoje</p>
        </div>
        <MetaAguaDialog metaMl={resumo.metaMl} onAtualizada={onMetaAtualizada} />
      </div>

      <Progress value={resumo.percentual} />

      <div className="flex flex-wrap gap-2">
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
