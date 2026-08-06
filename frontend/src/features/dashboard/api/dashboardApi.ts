import { axiosClient } from '@/lib/axiosClient'
import type { DashboardResponseDTO } from '@/types/dashboard'

export async function obterDashboard() {
  const { data } = await axiosClient.get<DashboardResponseDTO>('/dashboard')
  return data
}
