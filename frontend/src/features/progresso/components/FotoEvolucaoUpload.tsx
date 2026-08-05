import { useRef, useState } from 'react'
import { Camera, ImageIcon } from 'lucide-react'

import { atualizarFotoProgresso } from '@/features/progresso/api/progressoApi'

interface FotoEvolucaoUploadProps {
  registroId: number
  fotoUrl: string | null
  onEnviada: (fotoUrl: string) => void
}

export function FotoEvolucaoUpload({ registroId, fotoUrl, onEnviada }: FotoEvolucaoUploadProps) {
  const inputRef = useRef<HTMLInputElement>(null)
  const [enviando, setEnviando] = useState(false)

  async function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const arquivo = event.target.files?.[0]
    if (!arquivo) return

    setEnviando(true)
    try {
      const { fotoUrl: novaUrl } = await atualizarFotoProgresso(registroId, arquivo)
      onEnviada(novaUrl)
    } finally {
      setEnviando(false)
      event.target.value = ''
    }
  }

  return (
    <button
      type="button"
      onClick={() => inputRef.current?.click()}
      disabled={enviando}
      className="group relative size-12 shrink-0 overflow-hidden rounded-lg border border-border bg-muted/40"
      aria-label="Foto de evolução"
    >
      {fotoUrl ? (
        <img src={fotoUrl} alt="Foto de evolução" className="size-full object-cover" />
      ) : (
        <span className="flex size-full items-center justify-center text-muted-foreground">
          <ImageIcon className="size-4" />
        </span>
      )}
      <span className="absolute inset-0 flex items-center justify-center bg-black/40 opacity-0 transition-opacity group-hover:opacity-100">
        <Camera className="size-4 text-white" />
      </span>
      <input ref={inputRef} type="file" accept="image/jpeg,image/png,image/webp" className="hidden" onChange={handleFileChange} />
    </button>
  )
}
