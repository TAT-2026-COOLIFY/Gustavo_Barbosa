import { Navigate, Outlet } from 'react-router-dom'

import { useAuth } from '@/features/auth/hooks/useAuth'

export function ProtectedRoute() {
  const { status } = useAuth()

  if (status === 'loading') {
    return (
      <div className="flex min-h-svh items-center justify-center bg-background">
        <div className="size-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }

  if (status === 'unauthenticated') {
    return <Navigate to="/login" replace />
  }

  return <Outlet />
}
