export interface ComentarioResponse {
  id: number;
  usuarioNickname: string;
  usuarioAvatarUrl?: string;
  comentario: string;
  fecha: string;
  idPadre?: number;
  respuestas?: ComentarioResponse[];

  isReplying?: boolean; // Para mostrar/ocultar el input de respuesta
  replyText?: string; // Texto de la respuesta en el input
}