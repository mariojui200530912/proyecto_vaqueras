import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RecargarRequest } from '../models/transaccion/RecargarRequest';
import { TransaccionResponse } from '../models/transaccion/Transaccion';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TransaccionService {
  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/transaccion`;

  recargar(request: RecargarRequest){
    return this.http.post<number>(`${this.apiUrl}/recarga`, request);
  }

  obtenerHistorial(idUsuario: number): Observable<TransaccionResponse[]> {
    return this.http.get<TransaccionResponse[]>(`${this.apiUrl}/${idUsuario}/historial`);
  }
}
