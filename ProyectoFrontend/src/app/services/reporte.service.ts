import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ReporteService {
 private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/reporte`;

  private descargarPDF(endpoint: string, params: any, nombreArchivo: string) {
    let httpParams = new HttpParams();
    Object.keys(params).forEach(key => {
      if (params[key] != null) httpParams = httpParams.set(key, params[key]);
    });

    this.http.get(`${this.apiUrl}/${endpoint}`, {
      params: httpParams,
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nombreArchivo;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Error descargando reporte', err)
    });
  }

  // --- ADMIN ---
  descargarGanancias(inicio: string, fin: string) {
    this.descargarPDF('ganancias', { inicio, fin }, 'ganancias_globales.pdf');
  }
  descargarTopVentas(inicio: string, fin: string, categoria?: number, clasificacion?: string) {
    const params = { 
      inicio, 
      fin, 
      categoria, 
      clasificacion 
    };
    this.descargarPDF('top-calidad', params, 'top_ventas_calidad.pdf');
  }
  descargarIngresosEmpresas(inicio: string, fin: string) {
    this.descargarPDF('empresas', { inicio, fin }, 'ingresos_empresas.pdf');
  }
  descargarRankingUsuarios() {
    this.descargarPDF('usuarios', {}, 'ranking_usuarios.pdf');
  }

  // --- EMPRESA ---
  descargarVentasPropias(inicio: string, fin: string) {
    // Falta mandar el id de la empresa autenticada
    this.descargarPDF('empresa/ventas', { inicio, fin }, 'mis_ventas.pdf');
  }
  descargarTop5Propios(inicio: string, fin: string) {
    this.descargarPDF('empresa/top5', { inicio, fin }, 'mis_top5_juegos.pdf');
  }
  descargarFeedback() {
    this.descargarPDF('empresa/feedback', {}, 'mi_feedback.pdf');
  }

  // --- GAMER ---
  descargarGastos(idUsuario: number) {
    this.descargarPDF('usuario/gastos', { idUsuario }, 'historial_gastos.pdf');
  }
  descargarBiblioteca(idUsuario: number) {
    this.descargarPDF('usuario/biblioteca', { idUsuario }, 'analisis_biblioteca.pdf');
  }
  descargarPrestamos(idUsuario: number) {
    this.descargarPDF('usuario/prestamos', { idUsuario }, 'historial_prestamos.pdf');
  }
}
