import { Routes, Route } from 'react-router-dom'

import { SplashPage } from '@/features/auth/pages/SplashPage'
import { ComingSoonPage } from '@/components/shared/ComingSoonPage'

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<SplashPage />} />
      <Route path="/login" element={<ComingSoonPage title="Entrar" />} />
      <Route path="/cadastro" element={<ComingSoonPage title="Criar conta" />} />
    </Routes>
  )
}
