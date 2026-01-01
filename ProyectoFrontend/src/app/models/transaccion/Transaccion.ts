export interface TransaccionResponse {
  id: number;
  fecha: string;
  monto: number;
  tipo: string; // 'RECARGA', 'COMPRA', etc.
  descripcion?: string;
}