export interface UsuarioResponseDTO {
  id: number
  nome: string
  email: string
  fotoPerfilUrl: string | null
}

export interface LoginResponseDTO {
  accessToken: string
  refreshToken: string
  expiresInSeconds: number
  usuario: UsuarioResponseDTO
}

export interface ApiErrorResponse {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  fieldErrors: { field: string; message: string }[]
}
