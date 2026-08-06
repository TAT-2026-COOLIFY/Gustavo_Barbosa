import type { DiarioHistoricoDiaDTO } from '@/types/dieta'

interface CaloriasChartProps {
  historico: DiarioHistoricoDiaDTO[]
}

const LABEL_DIA_SEMANA = ['dom', 'seg', 'ter', 'qua', 'qui', 'sex', 'sáb']

export function CaloriasChart({ historico }: CaloriasChartProps) {
  if (historico.length === 0) {
    return <p className="text-sm text-muted-foreground">Sem registros no período.</p>
  }

  const maiorValor = Math.max(
    ...historico.map((dia) => dia.caloriasConsumidas),
    ...historico.map((dia) => dia.metaCaloriasKcal ?? 0),
    1,
  )

  return (
    <div className="flex items-end justify-between gap-2 pt-4" style={{ height: '9rem' }}>
      {historico.map((dia) => {
        const data = new Date(`${dia.data}T00:00:00`)
        const alturaBarra = Math.max((dia.caloriasConsumidas / maiorValor) * 100, 2)
        const alturaMeta = dia.metaCaloriasKcal ? Math.min((dia.metaCaloriasKcal / maiorValor) * 100, 100) : null
        const atingiu = dia.metaCaloriasKcal != null && dia.caloriasConsumidas >= dia.metaCaloriasKcal

        return (
          <div key={dia.data} className="flex h-full flex-1 flex-col items-center justify-end gap-1.5">
            <div className="relative flex h-full w-full max-w-8 items-end justify-center">
              {alturaMeta !== null && (
                <div
                  className="absolute w-full border-t-2 border-dashed border-muted-foreground/40"
                  style={{ bottom: `${alturaMeta}%` }}
                />
              )}
              <div
                className={`w-full rounded-t-md transition-all ${atingiu ? 'bg-primary' : 'bg-primary/40'}`}
                style={{ height: `${alturaBarra}%` }}
                title={`${Math.round(dia.caloriasConsumidas)} kcal${dia.metaCaloriasKcal ? ` de ${dia.metaCaloriasKcal} kcal` : ''}`}
              />
            </div>
            <span className="text-[11px] text-muted-foreground">{LABEL_DIA_SEMANA[data.getDay()]}</span>
          </div>
        )
      })}
    </div>
  )
}
