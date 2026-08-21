import { useEffect, useRef } from 'react'
import { animate, motion, useAnimationControls, useMotionValue, useTransform, type Transition } from 'framer-motion'

interface CopoAguaAnimadoProps {
  percentual: number
  totalMl: number
  metaMl: number
  pulseKey: number
}

const COPO_PATH = 'M14 8 H90 L78 148 Q76 154 70 154 H34 Q28 154 26 148 Z'

// topo do copo (y=14, cheio) até o fundo (y=160, vazio)
const NIVEL_TOPO = 14
const NIVEL_FUNDO = 160

// mola "mole" (stiffness baixo, damping proporcional) imita a inércia de líquido subindo, em vez de um
// objeto rígido — sobe com uma leve folga e assenta suave, sem o "solavanco" de uma mola mais dura
const NIVEL_TRANSICAO: Transition = { type: 'spring', stiffness: 34, damping: 11, mass: 1 }

// arrays de keyframe hospedados fora do componente: se fossem criados a cada render, a mudança de
// referência faria o framer-motion reiniciar o loop das ondas toda vez que o nível de água mudasse
const ONDA_TRAS_X = [-60, 0]
const ONDA_FRENTE_X = [0, -60]

export function CopoAguaAnimado({ percentual, totalMl, metaMl, pulseKey }: CopoAguaAnimadoProps) {
  const percentualVisual = Math.min(Math.max(percentual, 0), 100)
  const nivelAgua = NIVEL_FUNDO - (percentualVisual / 100) * (NIVEL_FUNDO - NIVEL_TOPO)

  // bounce de feedback ao registrar — via controls (não via remount por `key`), pra não interromper
  // o loop das ondas nem a subida da água já em andamento
  const bounceControls = useAnimationControls()
  const primeiraRenderizacao = useRef(true)
  useEffect(() => {
    if (primeiraRenderizacao.current) {
      primeiraRenderizacao.current = false
      return
    }
    bounceControls.start({ scale: [1, 1.05, 1], transition: { duration: 0.6, ease: 'easeOut' } })
  }, [pulseKey, bounceControls])

  // contador do total em ml sobe suavemente até o novo valor, em vez de saltar direto — o percentual
  // é derivado do mesmo valor animado, pra número e "%" nunca ficarem dessincronizados em pleno voo
  const totalMotion = useMotionValue(totalMl)
  const totalArredondado = useTransform(totalMotion, (valor) => Math.round(valor))
  const percentualAnimado = useTransform(totalMotion, (valor) =>
    metaMl > 0 ? Math.min(100, Math.round((valor * 100) / metaMl)) : 0,
  )
  useEffect(() => {
    const controls = animate(totalMotion, totalMl, { duration: 0.7, ease: 'easeOut' })
    return () => controls.stop()
  }, [totalMl, totalMotion])

  return (
    <div className="flex flex-col items-center gap-2 text-primary">
      <motion.div animate={bounceControls} initial={{ scale: 1 }}>
        <svg width="104" height="160" viewBox="0 0 104 160">
          <defs>
            <clipPath id="copo-agua-clip">
              <path d={COPO_PATH} />
            </clipPath>
            <linearGradient id="copo-agua-gradiente" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="currentColor" stopOpacity="0.85" />
              <stop offset="100%" stopColor="currentColor" stopOpacity="0.55" />
            </linearGradient>
          </defs>

          <path d={COPO_PATH} fill="none" stroke="currentColor" strokeOpacity="0.25" strokeWidth="2.5" />

          <g clipPath="url(#copo-agua-clip)">
            <motion.rect
              x="-10"
              width="124"
              height="220"
              fill="url(#copo-agua-gradiente)"
              initial={false}
              animate={{ y: nivelAgua }}
              transition={NIVEL_TRANSICAO}
            />

            {/* onda de trás: mais lenta e sutil, dá profundidade à superfície */}
            <motion.path
              d="M-30 0 Q -15 -6 0 0 T 30 0 T 60 0 T 90 0 T 120 0 T 150 0 V 14 H -30 Z"
              fill="currentColor"
              opacity="0.28"
              initial={false}
              animate={{ x: ONDA_TRAS_X, y: nivelAgua - 3 }}
              transition={{ x: { duration: 5, repeat: Infinity, ease: 'linear' }, y: NIVEL_TRANSICAO }}
            />

            {/* onda da frente: mais rápida, é o "brilho" que dá a sensação de movimento */}
            <motion.path
              d="M-30 0 Q -15 -8 0 0 T 30 0 T 60 0 T 90 0 T 120 0 T 150 0 V 12 H -30 Z"
              fill="currentColor"
              opacity="0.45"
              initial={false}
              animate={{ x: ONDA_FRENTE_X, y: nivelAgua - 5 }}
              transition={{ x: { duration: 3, repeat: Infinity, ease: 'linear' }, y: NIVEL_TRANSICAO }}
            />
          </g>
        </svg>
      </motion.div>

      <div className="text-center">
        <p className="text-2xl font-semibold text-foreground">
          <motion.span>{totalArredondado}</motion.span>{' '}
          <span className="text-sm font-normal text-muted-foreground">/ {metaMl} ml</span>
        </p>
        <p className="text-xs text-muted-foreground">
          <motion.span>{percentualAnimado}</motion.span>% da meta de hoje
        </p>
      </div>
    </div>
  )
}
