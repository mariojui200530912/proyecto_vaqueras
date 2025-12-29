import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { inject, signal } from '@angular/core';
import { Input, OnInit } from '@angular/core';
import { JuegoService } from '../../../services/juego.service';
import { CategoriaService } from '../../../services/categoria.service';
import { Categoria } from '../../../models/categoria/Categoria';

@Component({
  selector: 'app-moderacion-categorias',
  imports: [CommonModule, FormsModule],
  templateUrl: './moderacion-categorias.component.html',
  styleUrl: './moderacion-categorias.component.css',
})
export class ModeracionCategoriasComponent implements OnInit {
  private juegoService = inject(JuegoService);
  public categoriaService = inject(CategoriaService); // Para obtener la lista global

  // INPUT: Recibimos el ID del juego desde el componente padre
  @Input({ required: true }) idJuego!: number;

  // ESTADO
  categoriasAsignadas = signal<Categoria[]>([]);
  idCategoriaSeleccionada: number | undefined = undefined;
  isLoading = false;

  ngOnInit() {
    this.cargarCategoriasDelJuego();
    // Aseguramos que el servicio global tenga las categorías cargadas
    if (this.categoriaService.categorias().length === 0) {
      this.categoriaService.cargarCategorias();
    }
  }

  cargarCategoriasDelJuego() {
    this.juegoService.obtenerCategoriasDeJuego(this.idJuego).subscribe({
      next: (data) => this.categoriasAsignadas.set(data),
      error: (e) => console.error('Error cargando tags', e)
    });
  }

  // --- ACCIÓN: AGREGAR ---
  agregar() {
    if (!this.idCategoriaSeleccionada) return;

    this.isLoading = true;
    this.juegoService.agregarCategoria(this.idJuego, this.idCategoriaSeleccionada).subscribe({
      next: () => {
        this.isLoading = false;
        this.idCategoriaSeleccionada = undefined; // Limpiar select
        this.cargarCategoriasDelJuego(); // Refrescar lista visual
      },
      error: (err) => {
        this.isLoading = false;
        alert('Error: ' + (err.error?.mensaje || 'No se pudo agregar'));
      }
    });
  }

  // --- ACCIÓN: ELIMINAR ---
  eliminar(idCat: number) {
    if(!confirm('¿Quitar esta categoría del juego?')) return;

    this.juegoService.eliminarCategoria(this.idJuego, idCat).subscribe({
      next: () => {
        this.cargarCategoriasDelJuego(); // Refrescar lista visual
      },
      error: (err) => alert('Error: ' + (err.error?.mensaje || 'No se pudo eliminar'))
    });
  }

  // Helper para no mostrar en el Select las que ya tiene asignadas
  yaEstaAsignada(idCat: number): boolean {
    return this.categoriasAsignadas().some(c => c.id === idCat);
  }
}
