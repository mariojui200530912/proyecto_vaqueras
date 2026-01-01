export interface Biblioteca {
  idBiblioteca: number;
  idJuego: number;
  titulo: string;
  descripcion: string;
  clasificacion: string;
  portada?: string; // Base64
  fechaAdquisicion: string;
  jugadoHoras: number;
  estado: string;
}