import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { inject, signal, output } from '@angular/core';
import { Juego } from '../../../models/juego/Juego';
import { JuegoService } from '../../../services/juego.service';
import { CategoriaService } from '../../../services/categoria.service';

@Component({
  selector: 'app-buscador',
  imports: [CommonModule, FormsModule],
  templateUrl: './buscador.component.html',
  styleUrl: './buscador.component.css',
})
export class BuscadorComponent {
  private juegoService = inject(JuegoService);
  public categoriaService = inject(CategoriaService); // Para el dropdown

  juegoSeleccionado = output<Juego>();

  // Filtros
  filtroTitulo: string = '';
  filtroCategoria: number | undefined = undefined;

  // Estado
  resultados = signal<Juego[]>([]);
  buscando = signal<boolean>(false);
  haBuscado = signal<boolean>(false); // Para mostrar mensaje "No hay resultados"

  constructor() {
    // Cargar categorías si no están cargadas
    if (this.categoriaService.categorias().length === 0) {
      this.categoriaService.cargarCategorias();
    }
  }

  buscar() {
    // Evitar búsquedas vacías si quieres
    if (!this.filtroTitulo && !this.filtroCategoria) return;

    this.buscando.set(true);
    this.haBuscado.set(true);

    this.juegoService.buscarJuegos(this.filtroTitulo, this.filtroCategoria)
      .subscribe({
        next: (data) => {
          this.resultados.set(data);
          this.buscando.set(false);
        },
        error: (err) => {
          console.error(err);
          this.buscando.set(false);
        }
      });
  }

  seleccionar(juego: Juego) {
    // Emitimos el juego hacia arriba
    this.juegoSeleccionado.emit(juego);
    
    // Opcional: Limpiar la búsqueda después de seleccionar
    this.limpiar();
  }

  limpiar() {
    this.resultados.set([]);
    this.filtroTitulo = '';
    this.filtroCategoria = undefined;
    this.haBuscado.set(false);
  }
}
