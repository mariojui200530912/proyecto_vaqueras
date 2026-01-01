import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { signal } from '@angular/core';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Biblioteca } from '../models/biblioteca/Biblioteca';
import { Prestamo } from '../models/biblioteca/Prestamo';
import { MensajeResponse } from '../models/MensajeResponse';

@Injectable({
  providedIn: 'root',
})
export class BibliotecaService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl; 

  miBiblioteca = signal<Biblioteca[]>([]);
  misPrestamos = signal<Prestamo[]>([]);

  cargarBiblioteca(idUsuario: number) {
    this.http.get<Biblioteca[]>(`${this.apiUrl}/biblioteca/${idUsuario}`).subscribe({
      next: (data) => this.miBiblioteca.set(data),
      error: (e) => console.error('Error cargando biblioteca propia', e)
    });
  }

  cargarPrestamos(idUsuario: number) {
    this.http.get<Prestamo[]>(`${this.apiUrl}/prestamo/${idUsuario}`).subscribe({
      next: (data) => this.misPrestamos.set(data),
      error: (e) => console.error('Error cargando préstamos', e)
    });
  }

  // ACCIONES JUEGOS PROPIOS  
  cambiarEstadoInstalacionPropio(idJuego: number, idUsuario: number, instalar: boolean) {
    const params = new HttpParams().set('instalar', instalar.toString());
    return this.http.put<MensajeResponse>(`${this.apiUrl}/biblioteca/${idJuego}/instalar/${idUsuario}`, {}, { params }).pipe(
      tap(() => this.cargarBiblioteca(idUsuario))
    );
  }

  // ACCIONES JUEGOS PRESTADOS

  instalarPrestado(idPrestamo: number, idUsuario: number) {
    const params = new HttpParams().set('idUsuario', idUsuario);
    return this.http.put<MensajeResponse>(`${this.apiUrl}/prestamo/${idPrestamo}/instalar`, {}, { params }).pipe(
      tap(() => this.cargarPrestamos(idUsuario))
    );
  }

  desinstalarPrestado(idPrestamo: number, idUsuario: number) {
    const params = new HttpParams().set('idUsuario', idUsuario);
    return this.http.put<MensajeResponse>(`${this.apiUrl}/prestamo/${idPrestamo}/desinstalar`, {}, { params }).pipe(
      tap(() => this.cargarPrestamos(idUsuario))
    );
  }

  devolverPrestado(idPrestamo: number, idUsuario: number) {
    const params = new HttpParams().set('idUsuario', idUsuario);
    return this.http.delete<MensajeResponse>(`${this.apiUrl}/prestamo/${idPrestamo}`, { params }).pipe(
      tap(() => this.cargarPrestamos(idUsuario))
    );
  }

  tieneJuego(idUsuario: number, idJuego: number) {
    return this.http.get<boolean>(`${this.apiUrl}/biblioteca/${idJuego}/usuario/${idUsuario}`);
  }
}
