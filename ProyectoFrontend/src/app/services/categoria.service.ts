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
      next: (data) => {
        this.categorias.set(data);
      },
      error: (err) => {
        console.error('Error cargando categorías:', err);
      }
    });
  }

  guardar(cat: Categoria) {
    return this.http.post(this.apiUrl, cat).pipe(
      tap(() => this.cargarCategorias()) 
    );
  }

  eliminar(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.cargarCategorias()) 
    );
  }
}
