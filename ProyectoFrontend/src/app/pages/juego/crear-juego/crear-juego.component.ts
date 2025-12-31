import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { JuegoCreacion } from '../../../models/juego/JuegoCreacion';
import { JuegoService } from '../../../services/juego.service';
import { AuthService } from '../../../services/auth.service';
import { CategoriaService } from '../../../services/categoria.service';
import { Router } from '@angular/router';
import { Categoria } from '../../../models/categoria/Categoria';

@Component({
  selector: 'app-crear-juego.component',
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './crear-juego.component.html',
  styleUrl: './crear-juego.component.css',
})
export class CrearJuegoComponent {
  private juegoService = inject(JuegoService);
  public authService = inject(AuthService); // Public para usar en HTML
  public categoriaService = inject(CategoriaService);
  private router = inject(Router);

  categoriasSeleccionadas: number[] = [];
  isSubmitting = false;

  // Modelo de datos
  nuevoJuego: JuegoCreacion = {
    titulo: '',
    descripcion: '',
    precio: 0,
    recursosMinimos: '',
    clasificacion: 'E',
    categoriasIds: []
  };

  portadaSeleccionada: File | null = null;
  previewPortada: string | null = null;

  bannerSeleccionado: File | null = null;
  previewBanner: string | null = null;

  galeriaSeleccionada: File[] = [];

  onBannerSelected(event: any) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.bannerSeleccionado = file;

      const reader = new FileReader();
      reader.onload = e => this.previewBanner = e.target?.result as string;
      reader.readAsDataURL(file);
    }
  }

  onPortadaSelected(event: any) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.portadaSeleccionada = file;

      const reader = new FileReader();
      reader.onload = e => this.previewPortada = e.target?.result as string;
      reader.readAsDataURL(file);
    }
  }

  onGaleriaSelected(event: any) {
    if (event.target.files.length > 0) {
      this.galeriaSeleccionada = Array.from(event.target.files);
    }
  }

  toggleCategoria(id: number) {
    if (this.categoriasSeleccionadas.includes(id)) {
      this.categoriasSeleccionadas = this.categoriasSeleccionadas.filter(c => c !== id);
    } else {
      this.categoriasSeleccionadas.push(id);
    }
  }

  guardar() {
    this.nuevoJuego.categoriasIds = this.categoriasSeleccionadas;

    if (!this.nuevoJuego.titulo || !this.nuevoJuego.descripcion || !this.nuevoJuego.recursosMinimos) {
      alert('Por favor completa los campos obligatorios (*)');
      return;
    }

    if (!this.portadaSeleccionada) {
      alert('La imagen de portada es obligatoria.');
      return;
    }

    const usuario = this.authService.currentUser();
    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }

    if (this.nuevoJuego.categoriasIds.length === 0) {
        alert('Debes seleccionar al menos una categoría.');
        return;
    }

    this.isSubmitting = true;

    this.juegoService.publicarJuego(
      usuario.id, 
      this.nuevoJuego, 
      this.portadaSeleccionada, 
      this.galeriaSeleccionada
    ).subscribe({
      next: (juegoCreado: any) => {
        
        if (this.bannerSeleccionado && juegoCreado.id) {
            this.subirBannerYFinalizar(juegoCreado.id, juegoCreado.idEmpresa);
        } else {
            this.finalizar(juegoCreado.idEmpresa);
        }
      },
      error: (err) => {
        console.error(err);
        this.isSubmitting = false;
        alert('Error al publicar: ' + (err.error?.mensaje || err.message));
      }
    });
  }

  subirBannerYFinalizar(idJuego: number, idEmpresa: number) {
    if(!this.bannerSeleccionado) return;

    this.juegoService.subirBanner(idJuego, this.bannerSeleccionado).subscribe({
        next: () => this.finalizar(idEmpresa),
        error: (e) => {
            console.error('Error banner', e);
            // No detenemos el flujo, el juego ya se creó
            alert('Juego publicado, pero hubo un error subiendo el banner.');
            this.finalizar(idEmpresa);
        }
    });
  }

  finalizar(idEmpresa: number) {
    alert('¡Juego publicado exitosamente!');
    this.isSubmitting = false;
    this.router.navigate(['/empresa', idEmpresa]); 
  }
}