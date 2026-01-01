export interface JuegoGrupoResponse {
  idJuego: number;
  titulo: string;
  portada?: string;
  idDueno: number;
  nombreDueno: string;
  avatarDueno?: string;
}