import type { RegistroProgressoResponseDTO } from '@/types/progresso'

interface ComparacaoFotosCardProps {
  registros: RegistroProgressoResponseDTO[]
}

export function ComparacaoFotosCard({ registros }: ComparacaoFotosCardProps) {
  const comFoto = registros.filter((r) => r.fotoUrl).sort((a, b) => a.dataRegistro.localeCompare(b.dataRegistro))

  if (comFoto.length < 2) {
    return (
      <p className="text-sm text-muted-foreground">
        Envie fotos em ao menos dois registros para ver a comparação de evolução.
      </p>
    )
  }

  const antes = comFoto[0]
  const depois = comFoto[comFoto.length - 1]

  return (
    <div className="grid grid-cols-2 gap-3">
      {[
        { label: 'Antes', registro: antes },
        { label: 'Agora', registro: depois },
      ].map(({ label, registro }) => (
        <figure key={label} className="flex flex-col gap-1.5">
          <img
            src={registro.fotoUrl ?? undefined}
            alt={`Foto de evolução — ${label}`}
            className="aspect-3/4 w-full rounded-lg object-cover"
          />
          <figcaption className="text-center text-xs text-muted-foreground">
            {label} · {new Date(`${registro.dataRegistro}T00:00:00`).toLocaleDateString('pt-BR')} · {registro.pesoKg}kg
          </figcaption>
        </figure>
      ))}
    </div>
  )
}
