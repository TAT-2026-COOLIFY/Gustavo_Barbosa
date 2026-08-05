import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Check, X } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { formatarTempo, useElapsedSeconds } from '@/features/treinos/hooks/useElapsedSeconds'
import { RespiracaoAnimada } from '@/features/meditacao/components/RespiracaoAnimada'
import { concluirSessaoMeditacao, obterMeditacao, obterSessaoMeditacao } from '@/features/meditacao/api/meditacaoApi'
import type { MeditacaoResponseDTO, SessaoMeditacaoResponseDTO } from '@/types/meditacao'

export function ExecucaoMeditacaoPage() {
  const { sessaoId } = useParams()
  const navigate = useNavigate()
  const [sessao, setSessao] = useState<SessaoMeditacaoResponseDTO | null>(null)
  const [meditacao, setMeditacao] = useState<MeditacaoResponseDTO | null>(null)
  const [concluindo, setConcluindo] = useState(false)

  const elapsed = useElapsedSeconds(sessao?.dataRealizacao ?? null)

  useEffect(() => {
    obterSessaoMeditacao(Number(sessaoId)).then((data) => {
      setSessao(data)
      obterMeditacao(data.meditacaoId).then(setMeditacao)
    })
  }, [sessaoId])

  async function onConcluir() {
    setConcluindo(true)
    try {
      await concluirSessaoMeditacao(Number(sessaoId), elapsed)
      navigate('/meditacao/historico')
    } finally {
      setConcluindo(false)
    }
  }

  if (!sessao) {
    return (
      <div className="flex min-h-svh items-center justify-center bg-background">
        <div className="size-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }

  const respiracaoCompleta =
    sessao.tipo === 'RESPIRACAO' &&
    sessao.inspirarSegundos != null &&
    sessao.segurarSegundos != null &&
    sessao.expirarSegundos != null

  return (
    <div className="relative flex min-h-svh flex-col items-center justify-center gap-8 bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="absolute top-6 right-6">
        <Button
          variant="glass"
          size="icon"
          aria-label="Encerrar sem salvar"
          onClick={() => navigate('/meditacao')}
        >
          <X />
        </Button>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="flex w-full max-w-md flex-col items-center gap-8"
      >
        <div className="text-center">
          <h1 className="text-2xl font-semibold">{sessao.meditacaoNome}</h1>
          <p className="text-sm text-muted-foreground">Sugestão: {sessao.duracaoMinutosSugerida} min</p>
        </div>

        {respiracaoCompleta ? (
          <RespiracaoAnimada
            inspirarSegundos={sessao.inspirarSegundos!}
            segurarSegundos={sessao.segurarSegundos!}
            expirarSegundos={sessao.expirarSegundos!}
          />
        ) : (
          meditacao?.instrucoes && (
            <Card className="glass border-0">
              <CardContent className="py-4 text-center text-sm text-muted-foreground">{meditacao.instrucoes}</CardContent>
            </Card>
          )
        )}

        <p className="text-4xl font-semibold tabular-nums">{formatarTempo(elapsed)}</p>

        <Button size="lg" disabled={concluindo} onClick={onConcluir}>
          <Check />
          {concluindo ? 'Salvando...' : 'Concluir'}
        </Button>
      </motion.div>
    </div>
  )
}
