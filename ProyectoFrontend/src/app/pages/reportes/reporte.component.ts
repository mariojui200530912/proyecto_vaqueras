import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { inject } from '@angular/core';
import { ReporteService } from '../../services/reporte.service';
import { AuthService } from '../../services/auth.service';
import { CategoriaService } from '../../services/categoria.service';

@Component({
  selector: 'app-reporte.component',
  imports: [CommonModule, FormsModule],
  templateUrl: './reporte.component.html',
  styleUrl: './reporte.component.css',
})
export class ReporteComponent {
  private reporteService = inject(ReporteService);
  public categoriaService = inject(CategoriaService);
  public authService = inject(AuthService); // Público para usar en HTML

  // Filtros de fecha (Usados por Admin y Empresa)
  fechaInicio: string = '';
  fechaFin: string = '';
  filtroCategoria: number | undefined = undefined; // undefined = "Todas"
  filtroClasificacion: string | undefined = undefined; // undefined = "Todas"

  listaClasificaciones = ['E', 'T', 'M'];

  get fechasInvalidas(): boolean {
    return !this.fechaInicio || !this.fechaFin;
  }

  get usuarioActual() {
    return this.authService.currentUser();
  }

  // --- MÉTODOS ADMIN ---
  adminGanancias() { this.reporteService.descargarGanancias(this.fechaInicio, this.fechaFin); }
  adminTopVentas() { this.reporteService.descargarTopVentas(this.fechaInicio, this.fechaFin, this.filtroCategoria, this.filtroClasificacion); }
  adminEmpresas() { this.reporteService.descargarIngresosEmpresas(this.fechaInicio, this.fechaFin); }
  adminRanking() { this.reporteService.descargarRankingUsuarios(); }

  // --- MÉTODOS EMPRESA ---
  empresaVentas() { this.reporteService.descargarVentasPropias(this.fechaInicio, this.fechaFin); }
  empresaTop5() { this.reporteService.descargarTop5Propios(this.fechaInicio, this.fechaFin); }
  empresaFeedback() { this.reporteService.descargarFeedback(); }

  // --- MÉTODOS GAMER ---
  gamerGastos() { 
    if(this.usuarioActual) this.reporteService.descargarGastos(this.usuarioActual.id); 
  }
  gamerBiblioteca() { 
    if(this.usuarioActual) this.reporteService.descargarBiblioteca(this.usuarioActual.id); 
  }
  gamerPrestamos() { 
    if(this.usuarioActual) this.reporteService.descargarPrestamos(this.usuarioActual.id); 
  }
}
