export interface RegistroRequest {
  nickname: string;
  password: string;
  email: string;
  fechaNacimiento: string; // YYYY-MM-DD
  telefono: string;
  pais: string;
  rol: 'GAMER' | 'EMPRESA';
}