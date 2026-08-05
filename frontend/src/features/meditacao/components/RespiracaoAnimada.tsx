import { useEffect, useState } from 'react'

type Fase = 'inspirar' | 'segurar' | 'expirar'

const LABELS: Record<Fase, string> = { inspirar: 'Inspire', segurar: 'Segure', expirar: 'Expire' }
const ORDEM: Fase[] = ['inspirar', 'segurar', 'expirar']

interface RespiracaoAnimadaProps {
  inspirarSegundos: number
  segurarSegundos: number
  expirarSegundos: number
}

export function RespiracaoAnimada({ inspirarSegundos, segurarSegundos, expirarSegundos }: RespiracaoAnimadaProps) {
  const duracoes: Record<Fase, number> = {
    inspirar: inspirarSegundos,
    segurar: segurarSegundos,
    expirar: expirarSegundos,
  }

  const [fase, setFase] = useState<Fase>('inspirar')
  const [segundosRestantes, setSegundosRestantes] = useState(duracoes.inspirar || 1)
  const [scale, setScale] = useState(0.6)

  useEffect(() => {
    setSegundosRestantes(duracoes[fase] || 1)
    if (fase === 'inspirar') setScale(1)
    else if (fase === 'expirar') setScale(0.6)
    // segura o scale atual durante a fase "segurar"
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fase])

  useEffect(() => {
    const interval = setInterval(() => {
      setSegundosRestantes((atual) => {
        if (atual <= 1) {
          setFase((faseAtual) => proximaFase(faseAtual, duracoes))
          return 0
        }
        return atual - 1
      })
    }, 1000)
    return () => clearInterval(interval)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  return (
    <div className="flex flex-col items-center gap-6">
      <div
        className="relative flex items-center justify-center rounded-full bg-primary/20 transition-transform ease-in-out"
        style={{ width: 220, height: 220, transform: `scale(${scale})`, transitionDuration: `${duracoes[fase] || 1}s` }}
      >
        <div className="absolute inset-4 rounded-full bg-primary/30" />
        <span className="z-10 text-lg font-medium">{LABELS[fase]}</span>
      </div>
      <p className="text-3xl font-semibold tabular-nums">{segundosRestantes}</p>
    </div>
  )
}

function proximaFase(fase: Fase, duracoes: Record<Fase, number>): Fase {
  let index = ORDEM.indexOf(fase)
  for (let i = 0; i < ORDEM.length; i++) {
    index = (index + 1) % ORDEM.length
    if (duracoes[ORDEM[index]] > 0) return ORDEM[index]
  }
  return fase
}
