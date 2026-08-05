import { Trash2 } from 'lucide-react'

import { FotoEvolucaoUpload } from '@/features/progresso/components/FotoEvolucaoUpload'
import { RegistroProgressoDialog } from '@/features/progresso/components/RegistroProgressoDialog'
import type { RegistroProgressoResponseDTO } from '@/types/progresso'

interface HistoricoProgressoListProps {
  registros: RegistroProgressoResponseDTO[]
  onAtualizado: (registro: RegistroProgressoResponseDTO) => void
  onRemover: (id: number) => void
}

export function HistoricoProgressoList({ registros, onAtualizado, onRemover }: HistoricoProgressoListProps) {
  if (registros.length === 0) {
    return <p className="text-sm text-muted-foreground">Nenhum registro de progresso ainda.</p>
  }

  return (
    <div className="flex flex-col gap-2">
      {registros.map((registro) => (
        <div key={registro.id} className="flex items-center gap-3 rounded-lg bg-muted/40 px-3 py-2">
          <FotoEvolucaoUpload
            registroId={registro.id}
            fotoUrl={registro.fotoUrl}
            onEnviada={(fotoUrl) => onAtualizado({ ...registro, fotoUrl })}
          />

          <div className="flex-1 text-sm">
            <p className="font-medium">
              {registro.pesoKg}kg
              {registro.imc && <span className="font-normal text-muted-foreground"> · IMC {registro.imc}</span>}
            </p>
            <p className="text-xs text-muted-foreground">
              {new Date(`${registro.dataRegistro}T00:00:00`).toLocaleDateString('pt-BR')}
              {registro.percentualGordura && ` · ${registro.percentualGordura}% gordura`}
            </p>
          </div>

          <RegistroProgressoDialog registro={registro} onSalvo={onAtualizado} />
          <button
            type="button"
            aria-label="Remover registro"
            className="text-muted-foreground transition-colors hover:text-destructive"
            onClick={() => onRemover(registro.id)}
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ))}
    </div>
  )
}
