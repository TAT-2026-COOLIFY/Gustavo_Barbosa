export interface RegistroProgressoResponseDTO {
  id: number
  dataRegistro: string
  pesoKg: number
  imc: number | null
  percentualGordura: number | null
  massaMagraKg: number | null
  circunferenciaCinturaCm: number | null
  circunferenciaQuadrilCm: number | null
  circunferenciaBracoCm: number | null
  circunferenciaCoxaCm: number | null
  circunferenciaPeitoCm: number | null
  fotoUrl: string | null
  observacoes: string | null
}
