import { CommonModule } from '@angular/common';
import { Component, inject, signal, OnInit, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ComentarioResponse } from '../../../models/comentario/ComentarioResponse';
import { ComentarioService } from '../../../services/comentario.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-comentario',
  imports: [CommonModule, FormsModule],
  templateUrl: './comentario.component.html',
  styleUrl: './comentario.component.css',
})
export class ComentarioComponent {
  private commentsService = inject(ComentarioService);
  public authService = inject(AuthService);

  @Input({ required: true }) idJuego!: number;
  @Input() esPropietario: boolean = false;

  comentarios = signal<ComentarioResponse[]>([]);
  isLoading = signal<boolean>(false);
  // Nuevo comentario raíz
  nuevoComentario = signal<string>('');
  calificacionSeleccionada = signal<number>(0);
  isSubmitting = signal<boolean>(false);

  ngOnInit() {
    this.cargarComentarios();
  }

  cargarComentarios() {
    this.isLoading.set(true);
    this.commentsService.obtenerComentarios(this.idJuego).subscribe({
      next: (data) => {
        // El backend devuelve la lista en arbol 
        this.comentarios.set(data);
        this.isLoading.set(false);
      },
      error: (e) => {
        console.error('Error cargando comentarios', e);
        this.isLoading.set(false);
      }
    });
  }

  // Activa el modo respuesta para un comentario específico
  responder(comentario: ComentarioResponse) {
    comentario.isReplying = !comentario.isReplying;
    
    if (comentario.isReplying) {
      comentario.replyText = '';
    }
  }

  enviarRespuesta(comentarioPadre: ComentarioResponse) {
    const texto = comentarioPadre.replyText?.trim();
    if (!texto) return;

    this.procesarEnvioComentario(texto, comentarioPadre.id, () => {
      comentarioPadre.isReplying = false;
      comentarioPadre.replyText = '';
    });
  }

  enviarComentarioRaiz() {
    const texto = this.nuevoComentario().trim();
    this.procesarEnvioComentario(texto, undefined, () => this.nuevoComentario.set(''));
  }

  private procesarEnvioComentario(texto: string, idPadre: number | undefined, onSuccess: () => void) {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;

    this.isSubmitting.set(true);
    this.commentsService.publicarComentario(this.idJuego, userId, texto, idPadre).subscribe({
      next: () => {
        this.cargarComentarios(); // Recargamos para ver el árbol actualizado desde el backend
        onSuccess();
        this.isSubmitting.set(false);
      },
      error: (e) => {
        alert(e.error?.mensaje || 'Error al publicar.');
        this.isSubmitting.set(false);
      }
    });
  }

  setRating(stars: number) {
    if (!this.esPropietario) return;
    this.calificacionSeleccionada.set(stars);
  }

  enviarCalificacion() {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;

    this.isSubmitting.set(true);
    this.commentsService.calificar(this.idJuego, userId, this.calificacionSeleccionada()).subscribe({
      next: (res) => {
        alert(res.mensaje);
        this.isSubmitting.set(false);
      },
      error: (e) => {
        alert(e.error?.mensaje || 'Error al calificar');
        this.isSubmitting.set(false);
      }
    });
  }
}
