import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Usuario } from '../models/usuario/Usuario';
import { signal, inject } from '@angular/core';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/usuario`;

  usuarios = signal<Usuario[]>([]);
  usuariosGamer = signal<Usuario[]>([]);

  constructor() {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.http.get<Usuario[]>(this.apiUrl).subscribe({
      next: (data) => this.usuarios.set(data),
      error: (e) => console.error('Error cargando usuarios', e)
    });
  }

  cargarUsuariosGamer() {
    this.http.get<Usuario[]>(`${this.apiUrl}/gamers`).subscribe({
      next: (data) => this.usuariosGamer.set(data),
      error: (e) => console.error('Error cargando usuarios gamer', e)
    });
  }

  // REGISTRAR (POST con FormData)
  registrar(datos: any, avatar: File | null) {
    const formData = new FormData();
    formData.append('nickname', datos.nickname);
    formData.append('password', datos.password); // Obligatorio al crear
    formData.append('email', datos.email);
    formData.append('fechaNacimiento', datos.fechaNacimiento);
    formData.append('telefono', datos.telefono);
    formData.append('pais', datos.pais);
    formData.append('rol', datos.rol); // Solo Admin puede asignar rol al crear
    
    if (avatar) {
      formData.append('avatar', avatar);
    }

    return this.http.post<Usuario>(`${this.apiUrl}/registro`, formData).pipe(
      tap(() => this.cargarUsuarios())
    );
  }

  // ACTUALIZAR PERFIL (PUT con FormData)
  actualizar(id: number, datos: any, avatar: File | null) {
    const formData = new FormData();
    formData.append('nickname', datos.nickname);
    formData.append('email', datos.email);
    formData.append('fechaNacimiento', datos.fechaNacimiento);
    formData.append('telefono', datos.telefono);
    formData.append('pais', datos.pais);
    
    // Solo Admin puede enviar estos campos en el PUT
    if (datos.rol) formData.append('rol', datos.rol);
    if (datos.estado) formData.append('estado', datos.estado);

    if (avatar) {
      formData.append('avatar', avatar);
    }

    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, formData).pipe(
      tap(() => this.cargarUsuarios())
    );
  }

  // CAMBIAR ESTADO (PUT con JSON)
  cambiarEstado(id: number, nuevoEstado: 'ACTIVO' | 'INACTIVO' | 'SUSPENDIDO') {
    const body = { estado: nuevoEstado };
    return this.http.put(`${this.apiUrl}/${id}/estado`, body).pipe(
      tap(() => this.cargarUsuarios())
    );
  }

  eliminar(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.cargarUsuarios())
    );
  }
}
