import { Injectable } from '@angular/core';
import { inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Comision } from '../models/admin/Config';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ConfiguracionService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/admin`; 

  comisionActual = signal<number>(0); 

  constructor() {
    this.obtenerComisionActual();
  }
  // GET: Obtener Comisión Actual
  obtenerComisionActual() {
    this.http.get<Comision>(this.apiUrl).subscribe({
      next: (data) => this.comisionActual.set(data.comision),
      error: () => console.warn('No se pudo obtener la comisión actual')
    });
  }

  // POST: Actualizar Comisión
  actualizarComision(nuevoPorcentaje: number) {
    const body: Comision = { comision: nuevoPorcentaje };
    
    return this.http.post(`${this.apiUrl}/comision-global`, body).pipe(
      tap(() => {
        this.comisionActual.set(nuevoPorcentaje);
      })
    );
  }
}
