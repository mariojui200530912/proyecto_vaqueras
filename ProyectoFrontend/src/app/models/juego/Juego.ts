export interface Juego {
  id: number;
  titulo: string;
  idEmpresa: number;
  nombreEmpresa: string;
  descripcion: string;
  precio: number;
  recursosMinimos: string;
  clasificacion: string;
  calificacionPromedio: number;
  imagenPortada?: string; // El base64 de la imagen
  categorias?: string[];
}