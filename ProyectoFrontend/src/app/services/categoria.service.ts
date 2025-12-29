import { Injectable } from '@angular/core';
import { inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Categoria } from '../models/categoria/Categoria';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class CategoriaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/categoria`;

  categorias = signal<Categoria[]>([]);

  constructor() {
    this.cargarCategorias();
  }

  cargarCategorias() {
    this.http.get<Categoria[]>(this.apiUrl).subscribe({
      next: (data) => this.categorias.set(data),
      error: (e) => console.error('Error al cargar categorías', e)
    });
  }

  crear(categoria: Partial<Categoria>) {
    return this.http.post<Categoria>(this.apiUrl, categoria).pipe(
      tap(() => this.cargarCategorias()) // Recargar lista automáticamente
    );
  }

  actualizar(id: number, categoria: Partial<Categoria>) {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Categoria>(url, categoria).pipe(
      tap(() => this.cargarCategorias())
    );
  }

  eliminar(id: number) {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete(url).pipe(
      tap(() => this.cargarCategorias())
    );
  }
}
