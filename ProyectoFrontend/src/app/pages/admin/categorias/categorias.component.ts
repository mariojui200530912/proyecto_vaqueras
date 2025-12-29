import { Component } from '@angular/core';
import { inject } from '@angular/core';
import { CategoriaService } from '../../../services/categoria.service';
import { Categoria } from '../../../models/categoria/Categoria';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-categorias',
  imports: [CommonModule, FormsModule],
  templateUrl: './categorias.component.html',
  styleUrl: './categorias.component.css',
})
export class CategoriasComponent {
  public categoriaService = inject(CategoriaService);

  modoEdicion = false;
  categoriaForm: Categoria = { id: 0, nombre: '', descripcion: '' };

  iniciarCreacion() {
    this.modoEdicion = true;
    this.categoriaForm = { id: 0, nombre: '', descripcion: '' };
  }

  editar(cat: Categoria) {
    this.modoEdicion = true;
    this.categoriaForm = { ...cat };
  }

  cancelar() {
    this.modoEdicion = false;
    this.categoriaForm = { id: 0, nombre: '', descripcion: '' };
  }

  guardar() {
    if (!this.categoriaForm.nombre) return;

    const request = {
      nombre: this.categoriaForm.nombre,
      descripcion: this.categoriaForm.descripcion
    };

    if (this.categoriaForm.id && this.categoriaForm.id > 0) {
      
      // actualizar (PUT)
      this.categoriaService.actualizar(this.categoriaForm.id, request).subscribe({
        next: () => {
          alert('Categoría actualizada correctamente');
          this.cancelar();
        },
        error: (err) => alert('Error al actualizar: ' + (err.error?.mensaje || 'Error desconocido'))
      });

    } else {
      
      // crear (POST)
      this.categoriaService.crear(request).subscribe({
        next: () => {
          alert('Categoría creada correctamente');
          this.cancelar();
        },
        error: (err) => alert('Error al crear: ' + (err.error?.mensaje || 'Error desconocido'))
      });

    }
  }

  eliminar(id: number) {
    if(!confirm('¿Estás seguro de eliminar esta categoría?')) return;

    this.categoriaService.eliminar(id).subscribe({
      next: (res: any) => alert(res.mensaje || 'Categoría eliminada'),
      error: (err) => alert('Error: ' + (err.error?.mensaje || 'No se puede eliminar'))
    });
  }
}
