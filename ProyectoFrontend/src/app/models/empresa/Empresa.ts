export interface Empresa {
  id: number;
  nombre: string;
  descripcion: string;
  logo?: string; // Base64 o URL
  comisionEspecifica?: number;
  fechaCreacion?: Date;
  permiteComentarios?: boolean;
}