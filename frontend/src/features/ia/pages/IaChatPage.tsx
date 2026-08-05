import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { isAxiosError } from 'axios'
import { ArrowLeft, Send, Sparkles } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { buscarAlimentos } from '@/features/dieta/api/alimentoApi'
import { MensagemBubble } from '@/features/ia/components/MensagemBubble'
import { enviarMensagem } from '@/features/ia/api/iaApi'
import { listarExercicios } from '@/features/treinos/api/treinoApi'
import type { AlimentoResponseDTO } from '@/types/dieta'
import type { MensagemComRascunhoDTO } from '@/types/ia'
import type { ExercicioResponseDTO } from '@/types/treino'

export function IaChatPage() {
  const [mensagens, setMensagens] = useState<MensagemComRascunhoDTO[]>([])
  const [conversaId, setConversaId] = useState<number | null>(null)
  const [texto, setTexto] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState<string | null>(null)
  const [exercicios, setExercicios] = useState<ExercicioResponseDTO[]>([])
  const [alimentos, setAlimentos] = useState<AlimentoResponseDTO[]>([])
  const fimDaListaRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    listarExercicios().then(setExercicios)
    buscarAlimentos().then(setAlimentos)
  }, [])

  useEffect(() => {
    fimDaListaRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [mensagens])

  async function onEnviar() {
    const mensagem = texto.trim()
    if (!mensagem || enviando) return

    setErro(null)
    setEnviando(true)
    setTexto('')
    try {
      const resultado = await enviarMensagem(mensagem, conversaId ?? undefined)
      setConversaId(resultado.conversaId)
      setMensagens((atual) => [
        ...atual,
        { ...resultado.mensagemUsuario, rascunho: null },
        { ...resultado.mensagemAssistente, rascunho: resultado.rascunho },
      ])
    } catch (error) {
      const mensagemErro =
        isAxiosError(error) && error.response?.data?.message
          ? error.response.data.message
          : 'Não foi possível falar com o assistente agora. Tente novamente.'
      setErro(mensagemErro)
      setTexto(mensagem)
    } finally {
      setEnviando(false)
    }
  }

  function onDescartarRascunho(rascunhoId: number) {
    setMensagens((atual) =>
      atual.map((m) => {
        if (!m.rascunho || m.rascunho.id !== rascunhoId) return m
        return { ...m, rascunho: { ...m.rascunho, status: 'DESCARTADO' as const } }
      }),
    )
  }

  function onKeyDown(event: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      onEnviar()
    }
  }

  return (
    <div className="relative flex min-h-svh flex-col bg-background px-6 py-10">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute top-[-15%] left-1/2 h-[34rem] w-[34rem] -translate-x-1/2 rounded-full bg-primary opacity-30 blur-[110px]" />
      </div>

      <div className="mx-auto flex w-full max-w-2xl flex-1 flex-col gap-6">
        <div className="flex items-center justify-between">
          <Button asChild variant="glass" size="icon">
            <Link to="/app" aria-label="Voltar">
              <ArrowLeft />
            </Link>
          </Button>
          <ThemeToggle />
        </div>

        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="flex flex-1 flex-col gap-4"
        >
          <h1 className="flex items-center gap-2 text-2xl font-semibold">
            <Sparkles className="size-6 text-primary" />
            Assistente IronFit
          </h1>

          <div className="flex flex-1 flex-col gap-3 overflow-y-auto">
            {mensagens.length === 0 && (
              <p className="text-sm text-muted-foreground">
                Peça dicas de treino ou dieta, ou diga algo como "monta um treino de peito pra iniciante" ou "monta uma
                dieta pra emagrecimento".
              </p>
            )}
            {mensagens.map((mensagem) => (
              <MensagemBubble
                key={mensagem.id}
                mensagem={mensagem}
                exercicios={exercicios}
                alimentos={alimentos}
                onRascunhoDescartado={onDescartarRascunho}
              />
            ))}
            {enviando && <p className="text-sm text-muted-foreground">Pensando...</p>}
            {erro && <p className="text-sm text-destructive">{erro}</p>}
            <div ref={fimDaListaRef} />
          </div>

          <div className="glass flex items-end gap-2 rounded-2xl p-2">
            <Textarea
              value={texto}
              onChange={(e) => setTexto(e.target.value)}
              onKeyDown={onKeyDown}
              placeholder="Escreva sua mensagem..."
              className="min-h-11 flex-1 resize-none border-0 bg-transparent shadow-none focus-visible:ring-0"
              rows={1}
            />
            <Button size="icon" aria-label="Enviar" disabled={enviando || !texto.trim()} onClick={onEnviar}>
              <Send className="size-4" />
            </Button>
          </div>
        </motion.div>
      </div>
    </div>
  )
}
