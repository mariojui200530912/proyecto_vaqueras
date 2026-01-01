import { Usuario } from "../usuario/Usuario";

export interface GrupoResponse {
  id: number;
  nombre: string;
  idCreador: number;
  miembros: Usuario[]; 
}