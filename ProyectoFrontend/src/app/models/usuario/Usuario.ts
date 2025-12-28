export interface Usuario {
  id: number;
  nickname: string;
  email: string;
  fechaNacimiento: Date;
  telefono: string;
  pais: string;
  avatar?: string; // El base64 de la imagen
  rol: 'ADMIN' | 'EMPRESA' | 'GAMER';
  estado: 'ACTIVO' | 'INACTIVO' | 'SUSPENDIDO';
  carteraSaldo: number;
  fechaCreacion: Date;
}
