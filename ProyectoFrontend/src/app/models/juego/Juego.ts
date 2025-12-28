export interface Juego {
  id: number;
  titulo: string;
  nombreEmpresa: string;
  descripcion: string;
  precio: number;
  clasificacion: string;
  calificacionPromedio: number;
  imagenPortada?: string; // El base64 de la imagen
  categorias?: string[];
}