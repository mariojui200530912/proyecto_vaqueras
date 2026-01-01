import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ComentarioResponse } from '../models/comentario/ComentarioResponse';
import { ComentarioRequest } from '../models/comentario/ComentarioRequest';
import { MensajeResponse } from '../models/MensajeResponse';

@Injectable({
  providedIn: 'root',
})
export class ComentarioService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl; 

  // --- COMENTARIOS (JSON) ---
  
  obtenerComentarios(idJuego: number): Observable<ComentarioResponse[]> {
    return this.http.get<ComentarioResponse[]>(`${this.apiUrl}/comentario/${idJuego}/comentarios`);
  }

  publicarComentario(idJuego: number, idUsuario: number, texto: string, idPadre?: number): Observable<MensajeResponse> {
    const params = new HttpParams().set('idUsuario', idUsuario);
    
    const body: ComentarioRequest = {
      comentario: texto,
      idComentarioPadre: idPadre || null
    };

    return this.http.post<MensajeResponse>(`${this.apiUrl}/comentario/${idJuego}/comentar`, body, { params });
  }

  // --- CALIFICACIÓN (FORM-URLENCODED) ---

  calificar(idJuego: number, idUsuario: number, puntaje: number): Observable<MensajeResponse> {
    // El backend usa @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // Angular convierte automáticamente HttpParams a este formato.
    const body = new HttpParams()
      .set('idUsuario', idUsuario)
      .set('puntaje', puntaje);

    return this.http.post<MensajeResponse>(`${this.apiUrl}/calificacion/${idJuego}/calificar`, body);
  }
}
