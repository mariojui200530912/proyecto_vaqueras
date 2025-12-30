import { Injectable } from '@angular/core';
import { inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Empresa } from '../models/empresa/Empresa';
import { UsuarioEmpresa } from '../models/usuario/UsuarioEmpresa';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class EmpresaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/empresa`;

  empresas = signal<Empresa[]>([]);

  constructor() {
    this.cargarEmpresas();
  }

  cargarEmpresas() {
    this.http.get<Empresa[]>(this.apiUrl).subscribe({
      next: (data) => this.empresas.set(data),
      error: (e) => console.error(e)
    });
  }

  // CREAR
  crear(datos: Partial<Empresa>, logo: File | null) {
    const formData = new FormData();
    formData.append('datos', JSON.stringify(datos));
    if (logo) formData.append('logo', logo);

    return this.http.post<Empresa>(this.apiUrl, formData).pipe(
      tap(() => this.cargarEmpresas())
    );
  }

  obtenerPorId(id: number) {
    return this.http.get<Empresa>(`${this.apiUrl}/${id}`);
  }

  // ACTUALIZAR
  actualizar(id: number, datos: Partial<Empresa>, logo: File | null) {
    const formData = new FormData();
    formData.append('datos', JSON.stringify(datos));
    if (logo) formData.append('logo', logo);

    return this.http.put<Empresa>(`${this.apiUrl}/${id}`, formData).pipe(
      tap(() => this.cargarEmpresas())
    );
  }

  // CAMBIAR COMISIÓN
  actualizarComision(id: number, nuevaComision: number) {
    const body = { 
        comision: nuevaComision 
    };
    
    return this.http.patch(`${this.apiUrl}/${id}/comision`, body).pipe(
        tap(() => this.cargarEmpresas())
    );
  }

  // CONFIGURAR COMENTARIOS
  configurarComentarios(id: number, permitir: boolean) {
    const body = new HttpParams().set('permitir', permitir);

    return this.http.patch(`${this.apiUrl}/${id}/configuracion/comentarios`, body, {
      headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' })
    }).pipe(tap(() => this.cargarEmpresas()));
  }

  eliminar(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.cargarEmpresas())
    );
  }

  // --- Empleados ---
  vincularUsuario(idEmpresa: number, idUsuario: number, rolEmpresa: string) {
    const body = {
      idUsuario: idUsuario,
      rolEmpresa: rolEmpresa
    };
    return this.http.post(`${this.apiUrl}/${idEmpresa}/usuarios`, body);
  }

  // DESVINCULAR
  desvincularUsuario(idUsuario: number) {
    return this.http.delete(`${this.apiUrl}/usuarios/${idUsuario}`);
  }

  // LISTAR PERSONAL ACTUAL
  listarEmpleados(idEmpresa: number) {
      return this.http.get<UsuarioEmpresa[]>(`${this.apiUrl}/${idEmpresa}/usuarios`);
  }
}
