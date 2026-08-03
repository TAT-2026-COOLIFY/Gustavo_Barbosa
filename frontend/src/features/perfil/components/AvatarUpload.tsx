import { useRef, useState } from 'react'
import { Camera } from 'lucide-react'

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { atualizarFoto } from '@/features/perfil/api/perfilApi'

interface AvatarUploadProps {
  nome: string
  fotoPerfilUrl: string | null
  onUploaded: (url: string) => void
}

export function AvatarUpload({ nome, fotoPerfilUrl, onUploaded }: AvatarUploadProps) {
  const inputRef = useRef<HTMLInputElement>(null)
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState<string | null>(null)

  const iniciais = nome
    .split(' ')
    .slice(0, 2)
    .map((parte) => parte[0])
    .join('')
    .toUpperCase()

  async function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const arquivo = event.target.files?.[0]
    if (!arquivo) return

    setErro(null)
    setEnviando(true)
    try {
      const { fotoPerfilUrl: novaUrl } = await atualizarFoto(arquivo)
      onUploaded(novaUrl)
    } catch {
      setErro('Não foi possível enviar a imagem. Use JPEG, PNG ou WEBP.')
    } finally {
      setEnviando(false)
      event.target.value = ''
    }
  }

  return (
    <div className="flex flex-col items-center gap-2">
      <button
        type="button"
        onClick={() => inputRef.current?.click()}
        className="group relative"
        disabled={enviando}
        aria-label="Alterar foto de perfil"
      >
        <Avatar className="size-24 border-2 border-border shadow-md">
          {fotoPerfilUrl && <AvatarImage src={fotoPerfilUrl} alt={nome} />}
          <AvatarFallback className="text-2xl">{iniciais}</AvatarFallback>
        </Avatar>
        <span className="absolute inset-0 flex items-center justify-center rounded-full bg-black/40 opacity-0 transition-opacity group-hover:opacity-100">
          <Camera className="size-6 text-white" />
        </span>
      </button>
      <input ref={inputRef} type="file" accept="image/jpeg,image/png,image/webp" className="hidden" onChange={handleFileChange} />
      {enviando && <p className="text-xs text-muted-foreground">Enviando...</p>}
      {erro && <p className="text-xs text-destructive">{erro}</p>}
    </div>
  )
}
