import { useEffect, useState } from 'react'
import { Timer } from 'lucide-react'

import { formatarTempo } from '@/features/treinos/hooks/useElapsedSeconds'

export function RestTimer({ segundos, onFim }: { segundos: number; onFim: () => void }) {
  const [restante, setRestante] = useState(segundos)

  useEffect(() => {
    if (restante <= 0) {
      onFim()
      return
    }
    const timeout = setTimeout(() => setRestante((atual) => atual - 1), 1000)
    return () => clearTimeout(timeout)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [restante])

  return (
    <div className="flex items-center gap-1.5 text-sm text-primary">
      <Timer className="size-3.5 animate-pulse" />
      Descanso: {formatarTempo(restante)}
    </div>
  )
}
