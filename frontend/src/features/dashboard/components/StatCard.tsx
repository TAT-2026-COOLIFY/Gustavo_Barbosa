import type { ReactNode } from 'react'

import { Card, CardContent } from '@/components/ui/card'

interface StatCardProps {
  icon: ReactNode
  label: string
  valor: ReactNode
  sublabel?: ReactNode
}

export function StatCard({ icon, label, valor, sublabel }: StatCardProps) {
  return (
    <Card className="glass border-0">
      <CardContent className="flex flex-col gap-1.5">
        <div className="flex items-center gap-2 text-muted-foreground">
          {icon}
          <span className="text-xs font-medium tracking-wide uppercase">{label}</span>
        </div>
        <span className="text-2xl font-semibold">{valor}</span>
        {sublabel && <span className="text-xs text-muted-foreground">{sublabel}</span>}
      </CardContent>
    </Card>
  )
}
