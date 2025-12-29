import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Juego } from '../models/juego/Juego';
import { Banner } from '../models/banner/Banner';
import { map } from 'rxjs/operators';
import { Categoria } from '../models/categoria/Categoria';

@Injectable({
  providedIn: 'root',
})
export class JuegoService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  // Signals para almacenar los datos
  bannerData = signal<Banner[]>([]);
  juegosCatalogo = signal<Juego[]>([]);
  juegosDestacados = signal<Juego[]>([]);
  isLoading = signal<boolean>(true);
  
  obtenerBanner() {
    return this.http.get<Banner[]>(`${this.apiUrl}/banner`);
  }
  
  obtenerJuegoPorId(id: number) {
    return this.http.get<Juego>(`${this.apiUrl}/juego/${id}`);
  }

  obtenerJuegos() {
    return this.http.get<Juego[]>(`${this.apiUrl}/juego/catalogo`);
  }

  obtenerDestacados() {
    return this.http.get<Juego[]>(`${this.apiUrl}/juego/destacados`);
  }

  buscarJuegos(titulo?: string, idCategoria?: number, min?: number, max?: number) {
    let params = new HttpParams();
    if (titulo) params = params.set('titulo', titulo);
    if (idCategoria) params = params.set('categoria', idCategoria);
    if (min) params = params.set('min', min);
    if (max) params = params.set('max', max);

    return this.http.get<Juego[]>(`${this.apiUrl}/juego/buscar`, { params });
  }

  actualizarDatosJuego(idJuego: number, idUsuario: number, datos: any) {
    const formData = new FormData();
    formData.append('idUsuario', idUsuario.toString());
    // El backend espera un String JSON en el campo "datos"
    formData.append('datos', JSON.stringify(datos));

    return this.http.post(`${this.apiUrl}/${idJuego}/actualizar`, formData);
  }

  actualizarPortada(idJuego: number, archivo: File) {
    const formData = new FormData();
    formData.append('portada', archivo);
    return this.http.put(`${this.apiUrl}/${idJuego}/actualizar/imagen`, formData);
  }

  obtenerCategoriasDeJuego(idJuego: number) {
    return this.http.get<Categoria[]>(`${this.apiUrl}/${idJuego}/categorias`);
  }
  // Agregar una categoría a un juego
  agregarCategoria(idJuego: number, idCategoria: number) {
    return this.http.post(`${this.apiUrl}/${idJuego}/categoria/${idCategoria}`, {});
  }
  // Elimina una categoría de un juego
  eliminarCategoria(idJuego: number, idCategoria: number) {
    const params = new HttpParams().set('idCategoria', idCategoria);
    return this.http.delete(`${this.apiUrl}/${idJuego}/categoria`, { params });
  }

  // Método maestro para cargar todo al inicio
  cargarInicio() {
    this.isLoading.set(true);

    this.obtenerBanner().subscribe({
      next: (data) => this.bannerData.set(data),
      error: (e) => console.error('Error banner', e)
    });

    this.obtenerJuegos().subscribe({
      next: (data) => {
        this.juegosCatalogo.set(data);
        this.isLoading.set(false);
      },
      error: (e) => {
        console.error('Error juegos', e);
        this.isLoading.set(false);
      }
    });

    this.obtenerDestacados().subscribe({
      next: (data) => this.juegosDestacados.set(data)
    });
  }
}
