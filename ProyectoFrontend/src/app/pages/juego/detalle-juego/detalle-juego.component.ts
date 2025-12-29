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
  isLoading = true;
  modoEdicion = false; // Controla si mostramos Inputs o Textos
  
  // Para subida de archivos
  nuevaPortada: File | null = null;

  ngOnInit() {
    // Obtener ID de la URL
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) this.cargarJuego(id);
    });
  }

  cargarJuego(id: number) {
    this.isLoading = true;
    this.juegoService.obtenerJuegoPorId(id).subscribe({
      next: (data) => {
        this.juego.set(data);
        this.isLoading = false;
      },
      error: () => {
        alert('Juego no encontrado');
        this.router.navigate(['/']);
      }
    });
  }

  // --- ACCIONES DE EDICIÓN (EMPRESA) ---
  activarEdicion() {
    this.modoEdicion = true;
  }

  cancelarEdicion() {
    this.modoEdicion = false;
    this.cargarJuego(this.juego()!.id); // Revertir cambios recargando
  }

  guardarCambios() {
    const j = this.juego();
    const user = this.authService.currentUser();
    if (!j || !user) return;

    // datos a actualizar
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
}
