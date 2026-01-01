import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { signal, inject } from '@angular/core';
import { tap } from 'rxjs/operators';
import { GrupoRequest } from '../models/grupo/GrupoRequest';
import { GrupoResponse } from '../models/grupo/GrupoResponse';
import { MensajeResponse } from '../models/MensajeResponse';
import { JuegoGrupoResponse } from '../models/grupo/JuegoGrupoResponse';

@Injectable({
  providedIn: 'root',
})
export class GrupoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/grupo`;

  miGrupo = signal<GrupoResponse | null>(null);

  // CREAR GRUPO
  crearGrupo(idUsuario: number, nombre: string) {
    const req: GrupoRequest = { nombre };
    return this.http.post<GrupoResponse>(`${this.apiUrl}?idUsuario=${idUsuario}`, req).pipe(
      tap((nuevoGrupo) => this.miGrupo.set(nuevoGrupo))
    );
  }

  // VER GRUPO (Por ID)
  obtenerGrupo(idGrupo: number) {
    return this.http.get<GrupoResponse>(`${this.apiUrl}/${idGrupo}`).pipe(
      tap((grupo) => this.miGrupo.set(grupo))
    );
  }

  // AGREGAR MIEMBRO
  agregarMiembro(idGrupo: number, idUsuarioNuevo: number) {
    const body = { idUsuario: idUsuarioNuevo, idGrupo: idGrupo };

    return this.http.post<MensajeResponse>(`${this.apiUrl}/${idGrupo}/miembros`, body).pipe(
      tap(() => this.recargarGrupo(idGrupo))
    );
  }

  // ELIMINAR MIEMBRO / SALIRSE
  eliminarMiembro(idGrupo: number, idUsuarioAExpulsar: number, idSolicitante: number) {
    return this.http.delete<MensajeResponse>(
      `${this.apiUrl}/${idGrupo}/miembros/${idUsuarioAExpulsar}?idSolicitante=${idSolicitante}`
    ).pipe(
      tap(() => {
        // Si me expulsé a mí mismo, limpio el grupo
        if (idUsuarioAExpulsar === idSolicitante) {
          this.miGrupo.set(null);
        } else {
          this.recargarGrupo(idGrupo);
        }
      })
    );
  }

  // ELIMINAR GRUPO COMPLETO
  eliminarGrupo(idGrupo: number, idUsuario: number) {
    return this.http.delete<MensajeResponse>(`${this.apiUrl}/${idGrupo}?idUsuario=${idUsuario}`).pipe(
      tap(() => this.miGrupo.set(null))
    );
  }

  private recargarGrupo(idGrupo: number) {
    this.obtenerGrupo(idGrupo).subscribe();
  }

  // BUSCAR GRUPO POR USUARIO
  obtenerGrupoPorUsuario(idUsuario: number) {
    return this.http.get<GrupoResponse>(`${this.apiUrl}/usuario/${idUsuario}`).pipe(
      tap((grupo) => {
        this.miGrupo.set(grupo || null);
      })
    );
  }

  // LISTAR JUEGOS DISPONIBLES EN EL GRUPO
  obtenerJuegosGrupo(idGrupo: number, idUsuario: number) {
    return this.http.get<JuegoGrupoResponse[]>(`${this.apiUrl}/${idGrupo}/juegos?idUsuario=${idUsuario}`);
  }
}
