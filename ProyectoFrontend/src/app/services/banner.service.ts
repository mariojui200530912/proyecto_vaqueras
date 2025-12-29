import { Injectable } from '@angular/core';
import { inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Banner } from '../models/banner/Banner';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class BannerService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/banner`;

  // Signal para mantener el estado del banner actualizado en toda la app
  bannerItems = signal<Banner[]>([]);

  constructor() {
    this.cargarBanner();
  }

  cargarBanner() {
    this.http.get<Banner[]>(this.apiUrl).subscribe({
      next: (data) => this.bannerItems.set(data.sort((a, b) => a.orden - b.orden)),
      error: (e) => console.error('Error cargando banner', e)
    });
  }

  // POST: Agregar 
  agregarAlBanner(idJuego: number) {
    const params = new HttpParams().set('idJuego', idJuego);
    return this.http.post(this.apiUrl, {}, { params }).pipe(
      tap(() => this.cargarBanner())
    );
  }

  // DELETE: Quitar 
  quitarDelBanner(idJuego: number) {
    return this.http.delete(`${this.apiUrl}/${idJuego}`).pipe(
      tap(() => this.cargarBanner())
    );
  }

  // PUT: Reordenar
  cambiarOrden(idJuego: number, nuevoOrden: number) {

    return this.http.put(`${this.apiUrl}/${idJuego}/orden/${nuevoOrden}`,{}).pipe(
      tap(() => this.cargarBanner())
    );
  }
}
