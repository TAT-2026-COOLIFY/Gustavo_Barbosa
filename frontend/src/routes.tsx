import { Routes, Route } from 'react-router-dom'

import { ProtectedRoute } from '@/components/shared/ProtectedRoute'
import { SplashPage } from '@/features/auth/pages/SplashPage'
import { LoginPage } from '@/features/auth/pages/LoginPage'
import { CadastroPage } from '@/features/auth/pages/CadastroPage'
import { ForgotPasswordPage } from '@/features/auth/pages/ForgotPasswordPage'
import { ResetPasswordPage } from '@/features/auth/pages/ResetPasswordPage'
import { DashboardPlaceholderPage } from '@/features/dashboard/pages/DashboardPlaceholderPage'
import { PerfilPage } from '@/features/perfil/pages/PerfilPage'
import { TreinosListPage } from '@/features/treinos/pages/TreinosListPage'
import { TreinoFormPage } from '@/features/treinos/pages/TreinoFormPage'
import { ExecucaoTreinoPage } from '@/features/treinos/pages/ExecucaoTreinoPage'
import { HistoricoTreinosPage } from '@/features/treinos/pages/HistoricoTreinosPage'
import { DietasListPage } from '@/features/dieta/pages/DietasListPage'
import { DietaFormPage } from '@/features/dieta/pages/DietaFormPage'
import { DiarioPage } from '@/features/dieta/pages/DiarioPage'
import { HidratacaoPage } from '@/features/hidratacao/pages/HidratacaoPage'
import { ProgressoPage } from '@/features/progresso/pages/ProgressoPage'
import { IaChatPage } from '@/features/ia/pages/IaChatPage'
import { MeditacoesListPage } from '@/features/meditacao/pages/MeditacoesListPage'
import { ExecucaoMeditacaoPage } from '@/features/meditacao/pages/ExecucaoMeditacaoPage'
import { HistoricoMeditacaoPage } from '@/features/meditacao/pages/HistoricoMeditacaoPage'
import { NotificacoesPage } from '@/features/notificacoes/pages/NotificacoesPage'

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
        <Route path="/perfil" element={<PerfilPage />} />
        <Route path="/treinos" element={<TreinosListPage />} />
        <Route path="/treinos/novo" element={<TreinoFormPage />} />
        <Route path="/treinos/:id/editar" element={<TreinoFormPage />} />
        <Route path="/treinos/execucao/:sessaoId" element={<ExecucaoTreinoPage />} />
        <Route path="/treinos/historico" element={<HistoricoTreinosPage />} />
        <Route path="/dietas" element={<DietasListPage />} />
        <Route path="/dietas/novo" element={<DietaFormPage />} />
        <Route path="/dietas/:id/editar" element={<DietaFormPage />} />
        <Route path="/dietas/diario" element={<DiarioPage />} />
        <Route path="/hidratacao" element={<HidratacaoPage />} />
        <Route path="/progresso" element={<ProgressoPage />} />
        <Route path="/ia" element={<IaChatPage />} />
        <Route path="/meditacao" element={<MeditacoesListPage />} />
        <Route path="/meditacao/execucao/:sessaoId" element={<ExecucaoMeditacaoPage />} />
        <Route path="/meditacao/historico" element={<HistoricoMeditacaoPage />} />
        <Route path="/notificacoes" element={<NotificacoesPage />} />
      </Route>
    </Routes>
  )
}
