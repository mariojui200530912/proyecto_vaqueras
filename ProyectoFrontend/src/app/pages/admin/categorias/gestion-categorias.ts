import { Component } from '@angular/core';
import { inject } from '@angular/core';
import { CategoriaService } from '../../../services/categoria.service';
import { Categoria } from '../../../models/categoria/Categoria';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-gestion-categorias',
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-categorias.html',
  styleUrl: './gestion-categorias.css',
})
export class GestionCategorias {
  public categoriaService = inject(CategoriaService);

  // Estado del formulario
  modoEdicion = false;
  
  // Objeto temporal para el formulario
  categoriaForm: Categoria = { id: 0, nombre: '', descripcion: '' };

  // --- ACCIONES ---

  iniciarCreacion() {
    this.modoEdicion = true;
    this.categoriaForm = { id: 0, nombre: '', descripcion: '' }; // Limpiar
  }

  editar(cat: Categoria) {
    this.modoEdicion = true;
    // Copiamos el objeto para no modificar la tabla en tiempo real mientras escribe
    this.categoriaForm = { ...cat }; 
  }

  cancelar() {
    this.modoEdicion = false;
    this.categoriaForm = { id: 0, nombre: '', descripcion: '' };
  }

  guardar() {
    if (!this.categoriaForm.nombre) return;

    this.categoriaService.guardar(this.categoriaForm).subscribe({
      next: () => {
        alert('Categoría guardada correctamente');
        this.cancelar(); // Cierra formulario y limpia
      },
      error: (err) => alert('Error al guardar: ' + err.error)
    });
  }

  eliminar(id: number) {
    if(!confirm('¿Estás seguro? Si la categoría tiene juegos, no se podrá borrar.')) return;

    this.categoriaService.eliminar(id).subscribe({
      next: () => alert('Categoría eliminada'),
      error: (err) => alert('Error al eliminar. Verifique que no tenga juegos asociados.')
    });
  }
}
