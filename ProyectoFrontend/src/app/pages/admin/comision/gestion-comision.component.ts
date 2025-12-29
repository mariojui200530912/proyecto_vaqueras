import { Component } from '@angular/core';
import { inject } from '@angular/core';
import { ConfiguracionService } from '../../../services/configuracion.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-gestion-comision.component',
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-comision.component.html',
  styleUrl: './gestion-comision.component.css',
})
export class GestionComisionComponent {
  public configService = inject(ConfiguracionService);

  // Variable temporal para el input
  nuevoPorcentaje: number | null = null;
  isLoading = false;

  // Lógica de Guardado
  guardarCambios() {
    if (this.nuevoPorcentaje === null) return;
    
    // Validaciones
    if (this.nuevoPorcentaje < 0 || this.nuevoPorcentaje > 100) {
      alert('El porcentaje debe estar entre 0 y 100.');
      return;
    }

    if (!confirm(`¿Estás seguro de cambiar la comisión global al ${this.nuevoPorcentaje}%? Esto afectará a las futuras ventas.`)) {
      return;
    }

    this.isLoading = true;

    this.configService.actualizarComision(this.nuevoPorcentaje).subscribe({
      next: () => {
        alert('Comisión actualizada correctamente.');
        this.isLoading = false;
        this.nuevoPorcentaje = null; // Limpiar input
      },
      error: (err) => {
        console.error(err);
        alert('Error al actualizar: ' + (err.error?.mensaje || 'Error desconocido'));
        this.isLoading = false;
      }
    });
  }

  // Helper para pre-llenar el input con el valor actual
  usarValorActual() {
    this.nuevoPorcentaje = this.configService.comisionActual();
  }
}
