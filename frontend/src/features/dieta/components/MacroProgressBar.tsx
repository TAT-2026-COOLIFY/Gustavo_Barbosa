import { Progress } from '@/components/ui/progress'

interface MacroProgressBarProps {
  label: string
  consumido: number
  meta: number
  unidade: string
}

export function MacroProgressBar({ label, consumido, meta, unidade }: MacroProgressBarProps) {
  const percentual = meta > 0 ? Math.round((consumido / meta) * 100) : 0

  return (
    <div className="flex flex-col gap-1.5">
      <div className="flex items-center justify-between text-sm">
        <span className="font-medium">{label}</span>
        <span className="text-muted-foreground">
          {Math.round(consumido)}
          {unidade} / {meta}
          {unidade}
        </span>
      </div>
      <Progress value={percentual} />
    </div>
  )
}
