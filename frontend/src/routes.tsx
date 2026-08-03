import { Routes, Route } from 'react-router-dom'

import { ProtectedRoute } from '@/components/shared/ProtectedRoute'
import { SplashPage } from '@/features/auth/pages/SplashPage'
import { LoginPage } from '@/features/auth/pages/LoginPage'
import { CadastroPage } from '@/features/auth/pages/CadastroPage'
import { ForgotPasswordPage } from '@/features/auth/pages/ForgotPasswordPage'
import { ResetPasswordPage } from '@/features/auth/pages/ResetPasswordPage'
import { DashboardPlaceholderPage } from '@/features/dashboard/pages/DashboardPlaceholderPage'

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<SplashPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/cadastro" element={<CadastroPage />} />
      <Route path="/esqueci-senha" element={<ForgotPasswordPage />} />
      <Route path="/redefinir-senha" element={<ResetPasswordPage />} />

      <Route element={<ProtectedRoute />}>
        <Route path="/app" element={<DashboardPlaceholderPage />} />
      </Route>
    </Routes>
  )
}
