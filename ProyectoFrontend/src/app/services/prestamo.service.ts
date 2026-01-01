import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { MensajeResponse } from '../models/MensajeResponse';
import { Prestamo } from '../models/biblioteca/Prestamo';

@Injectable({
  providedIn: 'root',
})
export class PrestamoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/prestamo`; 

  solicitarPrestamo(idBeneficiario: number, request: { idJuego: number, idDueno: number }) {
    // El backend espera el ID del solicitante como QueryParam (?idUsuario=X)
    const params = new HttpParams().set('idUsuario', idBeneficiario.toString());

    return this.http.post<MensajeResponse>(this.apiUrl, request, { params });
  }

}
