export interface Prestamo {
  idPrestamo: number;
  idJuego: number;
  titulo: string;
  descripcion: string;
  clasificacion: string;
  portada?: string;
  nombreDueno: string;     // Info del dueño
  idDueno: number;
  estado: string;  
  fechaPrestamo?: string;
}