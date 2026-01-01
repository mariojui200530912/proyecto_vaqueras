import { Component } from '@angular/core';
import { inject, signal } from '@angular/core';
import { OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { TransaccionService } from '../../services/transaccion.service';
import { TransaccionResponse } from '../../models/transaccion/Transaccion';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-transaccion.component',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transaccion.component.html',
  styleUrl: './transaccion.component.css',
})
export class TransaccionComponent implements OnInit {
  private transaccionService = inject(TransaccionService);
  public authService = inject(AuthService);
  private fb = inject(FormBuilder);

  // --- Signals (Estado Reactivo) ---
  user = this.authService.currentUser;
  historial = signal<TransaccionResponse[]>([]);
  isLoading = signal<boolean>(false);
  mensajeEstado = signal<string>('');
  esError = signal<boolean>(false);

  // Formulario Reactivo
  recargaForm = this.fb.group({
    monto: [0, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.cargarHistorial();
  }

  cargarHistorial() {
    this.isLoading.set(true);
    this.transaccionService.obtenerHistorial(this.authService.currentUser()?.id!).subscribe({
      next: (data) => {
        this.historial.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error cargando historial', err);
        this.isLoading.set(false);
      }
    });
  }

  onRecargar() {
    if (this.recargaForm.invalid) return;

    const monto = this.recargaForm.value.monto ?? 0;
    const usuario = this.user(); // Obtenemos valor actual

    if (!usuario) return;

    this.isLoading.set(true);
    this.mensajeEstado.set('');

    const request = {
      idUsuario: usuario.id,
      monto: monto
    };

    this.transaccionService.recargar(request).subscribe({
      next: (res) => {
        this.authService.actualizarSaldo(res);

        this.mensajeEstado.set('¡Recarga exitosa! Créditos añadidos.');
        this.esError.set(false);
        this.recargaForm.reset({ monto: 0 });
        this.cargarHistorial(); 
        this.isLoading.set(false);
      },
      error: (err) => {
        this.mensajeEstado.set('Error al recargar: ' + err.error?.message);
        this.esError.set(true);
        this.isLoading.set(false);
      }
    });
  }
}
