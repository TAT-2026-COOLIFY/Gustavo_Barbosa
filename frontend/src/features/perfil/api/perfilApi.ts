import { axiosClient } from '@/lib/axiosClient'
import type { NivelExperiencia, ObjetivoResponseDTO, PerfilResponseDTO, Sexo } from '@/types/perfil'

export async function getPerfil() {
  const { data } = await axiosClient.get<PerfilResponseDTO>('/perfil')
  return data
}

export async function atualizarPerfil(input: {
  nome: string
  dataNascimento: string | null
  sexo: Sexo | null
  alturaCm: number | null
  nivelExperiencia: NivelExperiencia | null
  bio: string | null
}) {
  const { data } = await axiosClient.put<PerfilResponseDTO>('/perfil', input)
  return data
}

export async function atualizarFoto(arquivo: File) {
  const formData = new FormData()
  formData.append('arquivo', arquivo)
  const { data } = await axiosClient.post<{ fotoPerfilUrl: string }>('/perfil/foto', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}

export async function listarObjetivos() {
  const { data } = await axiosClient.get<ObjetivoResponseDTO[]>('/objetivos')
  return data
}

export async function getObjetivoAtivo() {
  const { data } = await axiosClient.get<ObjetivoResponseDTO>('/objetivos/ativo')
  return data
}

export async function criarObjetivo(input: { tipo: string; pesoMetaKg: number | null; dataAlvo: string | null }) {
  const { data } = await axiosClient.post<ObjetivoResponseDTO>('/objetivos', input)
  return data
}
