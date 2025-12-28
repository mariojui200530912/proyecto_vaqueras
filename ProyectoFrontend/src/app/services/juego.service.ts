import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Juego } from '../models/juego/Juego';
import { Banner } from '../models/banner/Banner';
import { map } from 'rxjs/operators';

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

  obtenerJuegos() {
    return this.http.get<Juego[]>(`${this.apiUrl}/juego/catalogo`);
  }

  obtenerDestacados() {
    return this.http.get<Juego[]>(`${this.apiUrl}/juego/destacados`);
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
