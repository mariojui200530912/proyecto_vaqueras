export interface Juego {
  id: number;
  titulo: string;
  idEmpresa: number;
  nombreEmpresa: string;
  descripcion: string;
  precio: number;
  recursosMinimos: string;
  clasificacion: string;
  fechaLanzamiento: Date;
  calificacionPromedio: number;
  portada?: string; // El base64 de la imagen
  categorias?: string[];
  estadoVenta: 'ACTIVO' | 'SUSPENDIDO';
  galeria?: ImagenGaleria[]; // Array de imágenes en base64
}

export interface ImagenGaleria{
  id: number;
  idJuego: number;
  imagen: string; // El base64 de la imagen
  atributo: string;
}