import type { HistoricoDiaHidratacaoDTO } from '@/types/hidratacao'

interface HistoricoAguaChartProps {
  historico: HistoricoDiaHidratacaoDTO[]
}

const LABEL_DIA_SEMANA = ['dom', 'seg', 'ter', 'qua', 'qui', 'sex', 'sáb']

export function HistoricoAguaChart({ historico }: HistoricoAguaChartProps) {
  if (historico.length === 0) {
    return <p className="text-sm text-muted-foreground">Sem registros no período.</p>
  }

  const maiorValor = Math.max(...historico.map((dia) => dia.totalMl), ...historico.map((dia) => dia.metaMl), 1)

  return (
    <div className="flex items-end justify-between gap-2 pt-4" style={{ height: '9rem' }}>
      {historico.map((dia) => {
        const data = new Date(`${dia.data}T00:00:00`)
        const alturaBarra = Math.max((dia.totalMl / maiorValor) * 100, 2)
        const alturaMeta = Math.min((dia.metaMl / maiorValor) * 100, 100)

        return (
          <div key={dia.data} className="flex h-full flex-1 flex-col items-center justify-end gap-1.5">
            <div className="relative flex h-full w-full max-w-8 items-end justify-center">
              <div
                className="absolute w-full border-t-2 border-dashed border-muted-foreground/40"
                style={{ bottom: `${alturaMeta}%` }}
              />
              <div
                className={`w-full rounded-t-md transition-all ${dia.atingiu ? 'bg-primary' : 'bg-primary/40'}`}
                style={{ height: `${alturaBarra}%` }}
                title={`${dia.totalMl}ml de ${dia.metaMl}ml`}
              />
            </div>
            <span className="text-[11px] text-muted-foreground">{LABEL_DIA_SEMANA[data.getDay()]}</span>
          </div>
        )
      })}
    </div>
  )
}
