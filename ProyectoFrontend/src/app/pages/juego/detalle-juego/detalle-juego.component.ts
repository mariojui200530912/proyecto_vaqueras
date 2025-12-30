import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { inject, signal } from '@angular/core';
import { JuegoService } from '../../../services/juego.service';
import { AuthService } from '../../../services/auth.service';
import { Juego } from '../../../models/juego/Juego';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ModeracionCategoriasComponent } from '../../admin/moderacion-categorias/moderacion-categorias.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-detalle-juego.component',
  imports: [CommonModule, FormsModule, RouterModule, ModeracionCategoriasComponent],
  templateUrl: './detalle-juego.component.html',
  styleUrl: './detalle-juego.component.css',
})
export class DetalleJuegoComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private juegoService = inject(JuegoService);
  public authService = inject(AuthService);

  // ESTADO
  juego = signal<Juego | null>(null);
  isLoading = signal<boolean>(true);
  modoEdicion = false; // Controla si mostramos Inputs o Textos
  // Para subida de archivos
  nuevaPortada: File | null = null;
  archivosGaleria: File[] = [];
  nuevoBanner: File | null = null;
  isUploading = signal<boolean>(false); // Para mostrar spinner en botones

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idStr = params.get('id');
      const id = Number(idStr);

      if (id && !isNaN(id) && id > 0) {
        this.cargarJuego(id);
      } else {
        console.error('ID inválido');
        this.isLoading.set(false); // Detener carga si no hay ID
        this.router.navigate(['/']);
      }
    });
  }

  cargarJuego(id: number) {
    // Usar .set() para signals
    this.isLoading.set(true); 

    this.juegoService.obtenerJuegoPorId(id).subscribe({
      next: (data) => {
        this.juego.set(data);
        this.isLoading.set(false); // Angular ahora SÍ detectará esto
      },
      error: (e) => {
        console.error('Error cargando juego:', e);
        this.isLoading.set(false); // Detener spinner aunque falle
        alert('Juego no encontrado o error de conexión');
        this.router.navigate(['/']);
      }
    });
  }

  // --- ACCIONES DE EDICIÓN ---
  activarEdicion() {
    this.modoEdicion = true;
  }

  cancelarEdicion() {
    this.modoEdicion = false;
    const currentId = this.juego()?.id;
    if (currentId) this.cargarJuego(currentId);
  }

  guardarCambios() {
    const j = this.juego();
    const user = this.authService.currentUser();
    if (!j || !user) return;

    const datosActualizar = {
      titulo: j.titulo,
      descripcion: j.descripcion,
      precio: j.precio,
      recursosMinimos: j.recursosMinimos,
      clasificacion: j.clasificacion 
    };

    this.juegoService.actualizarDatosJuego(j.id, user.id, datosActualizar).subscribe({
      next: () => {
        if (this.nuevaPortada) {
            this.juegoService.actualizarPortada(j.id, this.nuevaPortada).subscribe();
        }
        alert('Juego actualizado con éxito');
        this.modoEdicion = false;
        this.cargarJuego(j.id);
      },
      error: (e) => alert('Error al guardar: ' + e.message)
    });
  }

  onPortadaSelected(event: any) {
    this.nuevaPortada = event.target.files[0];
  }
  onGaleriaSelected(event: any) {
    // Convertimos FileList a Array
    if (event.target.files && event.target.files.length > 0) {
      this.archivosGaleria = Array.from(event.target.files);
    }
  }

  subirGaleria() {
    const j = this.juego();
    if (!j || this.archivosGaleria.length === 0) return;

    this.isUploading.set(true);

    this.juegoService.agregarImagenesGaleria(j.id, this.archivosGaleria).subscribe({
      next: () => {
        alert('Imágenes agregadas correctamente');
        this.archivosGaleria = []; // Limpiar selección
        this.isUploading.set(false);
        this.cargarJuego(j.id); // Recargar para ver las nuevas fotos
      },
      error: (e) => {
        this.isUploading.set(false);
        alert('Error al subir imágenes: ' + e.message);
      }
    });
  }

  eliminarImagen(idImagen: number) {
    const j = this.juego();
    if (!j || !confirm('¿Eliminar esta imagen de la galería?')) return;

    this.juegoService.eliminarImagenGaleria(j.id, idImagen).subscribe({
      next: () => this.cargarJuego(j.id),
      error: (e) => alert('Error: ' + e.message)
    });
  }

  // --- LÓGICA DE BANNER ---

  onBannerSelected(event: any) {
    this.nuevoBanner = event.target.files[0];
  }

  guardarBanner() {
    const j = this.juego();
    if (!j || !this.nuevoBanner) return;

    this.isUploading.set(true);
    this.juegoService.subirBanner(j.id, this.nuevoBanner).subscribe({
        next: () => {
            alert('Banner actualizado');
            this.isUploading.set(false);
            this.nuevoBanner = null;
            this.cargarJuego(j.id);
        },
        error: (e) => {
            this.isUploading.set(false);
            alert('Error: ' + e.message);
        }
    });
  }
}
