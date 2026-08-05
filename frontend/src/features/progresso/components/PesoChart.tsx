import type { RegistroProgressoResponseDTO } from '@/types/progresso'

interface PesoChartProps {
  registros: RegistroProgressoResponseDTO[]
  pesoMetaKg?: number | null
}

const LARGURA = 320
const ALTURA = 140
const PADDING = 24

export function PesoChart({ registros, pesoMetaKg }: PesoChartProps) {
  if (registros.length === 0) {
    return <p className="text-sm text-muted-foreground">Nenhum registro ainda.</p>
  }

  const ordenados = [...registros].sort((a, b) => a.dataRegistro.localeCompare(b.dataRegistro))
  const pesos = ordenados.map((r) => r.pesoKg)
  const valores = pesoMetaKg ? [...pesos, pesoMetaKg] : pesos
  const min = Math.min(...valores)
  const max = Math.max(...valores)
  const amplitude = max - min || 1

  function coordenadas(index: number, peso: number) {
    const x =
      ordenados.length === 1
        ? LARGURA / 2
        : PADDING + (index / (ordenados.length - 1)) * (LARGURA - PADDING * 2)
    const y = ALTURA - PADDING - ((peso - min) / amplitude) * (ALTURA - PADDING * 2)
    return { x, y }
  }

  const pontos = ordenados.map((r, i) => coordenadas(i, r.pesoKg))
  const pathLinha = pontos.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ')
  const yMeta = pesoMetaKg ? ALTURA - PADDING - ((pesoMetaKg - min) / amplitude) * (ALTURA - PADDING * 2) : null

  return (
    <div className="flex flex-col gap-2">
      <svg viewBox={`0 0 ${LARGURA} ${ALTURA}`} className="w-full" preserveAspectRatio="xMidYMid meet">
        {yMeta !== null && (
          <line
            x1={PADDING}
            y1={yMeta}
            x2={LARGURA - PADDING}
            y2={yMeta}
            strokeDasharray="4 3"
            className="stroke-muted-foreground/50"
            strokeWidth={1}
          />
        )}
        <path d={pathLinha} fill="none" className="stroke-primary" strokeWidth={2} />
        {pontos.map((p, i) => (
          <circle key={ordenados[i].id} cx={p.x} cy={p.y} r={3} className="fill-primary">
            <title>
              {ordenados[i].pesoKg}kg · {new Date(`${ordenados[i].dataRegistro}T00:00:00`).toLocaleDateString('pt-BR')}
            </title>
          </circle>
        ))}
      </svg>
      <div className="flex justify-between text-xs text-muted-foreground">
        <span>{new Date(`${ordenados[0].dataRegistro}T00:00:00`).toLocaleDateString('pt-BR')}</span>
        <span>{new Date(`${ordenados[ordenados.length - 1].dataRegistro}T00:00:00`).toLocaleDateString('pt-BR')}</span>
      </div>
    </div>
  )
}
