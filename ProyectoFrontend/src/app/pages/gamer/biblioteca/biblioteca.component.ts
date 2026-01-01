import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { inject, signal } from '@angular/core';
import { BibliotecaService } from '../../../services/biblioteca.service';
import { AuthService } from '../../../services/auth.service';
import { Biblioteca } from '../../../models/biblioteca/Biblioteca';
import { Prestamo } from '../../../models/biblioteca/Prestamo';
import { MensajeResponse } from '../../../models/MensajeResponse';

@Component({
  selector: 'app-biblioteca.component',
  imports: [CommonModule, RouterModule],
  templateUrl: './biblioteca.component.html',
  styleUrl: './biblioteca.component.css',
})
export class BibliotecaComponent implements OnInit {
  public bibliotecaService = inject(BibliotecaService);
  public authService = inject(AuthService);

  isLoading = signal<boolean>(true);
  filtroActual = signal<'TODOS' | 'PROPIOS' | 'PRESTADOS'>('TODOS');
  processingId = signal<number | null>(null); // Bloqueo de botones

  ngOnInit(): void {
    const userId = this.authService.currentUser()?.id;
    if (userId) {
      this.isLoading.set(true);
      this.bibliotecaService.cargarBiblioteca(userId);
      this.bibliotecaService.cargarPrestamos(userId);
      setTimeout(() => this.isLoading.set(false), 600);
    }
  }

  onInstalarPropio(juego: Biblioteca) {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;

    this.processingId.set(juego.idJuego);
    this.bibliotecaService.cambiarEstadoInstalacionPropio(juego.idJuego, userId, true).subscribe({
      next: (res) => {
        alert(res.mensaje); // "Juego instalado"
        this.processingId.set(null);
      },
      error: (err) => {
        alert('Error al instalar: ' + err.message);
        this.processingId.set(null);
      }
    });
  }

  onDesinstalarPropio(juego: Biblioteca) {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;
    
    // Confirmación opcional para propios
    if(!confirm('¿Desinstalar juego? Tendrás que descargarlo de nuevo para jugar.')) return;

    this.processingId.set(juego.idJuego);
    this.bibliotecaService.cambiarEstadoInstalacionPropio(juego.idJuego, userId, false).subscribe({
      next: (res) => {
        alert(res.mensaje);
        this.processingId.set(null);
      },
      error: (err) => {
        alert('Error: ' + err.message);
        this.processingId.set(null);
      }
    });
  }

  // --- GESTIÓN DE JUEGOS PRESTADOS ---

  onInstalarPrestado(item: Prestamo) {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;

    this.processingId.set(item.idJuego);
    this.bibliotecaService.instalarPrestado(item.idPrestamo, userId).subscribe({
      next: (res) => {
        alert(res.mensaje);
        this.processingId.set(null);
      },
      error: (err) => {
        // Error 409: Conflicto de restricción (Solo 1 prestado a la vez)
        const msg = err.error?.mensaje || 'Error: Solo puedes tener 1 juego prestado instalado simultáneamente.';
        alert(msg);
        this.processingId.set(null);
      }
    });
  }

  onDesinstalarPrestado(item: Prestamo) {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;
    
    if(!confirm(`¿Desinstalar ${item.titulo}? Liberarás el cupo para otro juego prestado.`)) return;

    this.processingId.set(item.idJuego);
    this.bibliotecaService.desinstalarPrestado(item.idPrestamo, userId).subscribe({
      next: (res) => {
        alert(res.mensaje);
        this.processingId.set(null);
      },
      error: (e) => {
        alert('Error: ' + e.message);
        this.processingId.set(null);
      }
    });
  }

  onDevolver(item: Prestamo) {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;

    if (item.estado === 'INSTALADO') {
      alert('Debes desinstalar el juego antes de devolverlo.');
      return;
    }

    if (!confirm('¿Devolver este juego permanentemente?')) return;

    this.processingId.set(item.idJuego);
    this.bibliotecaService.devolverPrestado(item.idPrestamo, userId).subscribe({
      next: (res) => {
        alert(res.mensaje);
        this.processingId.set(null);
      },
      error: (e) => {
        alert('Error: ' + e.message);
        this.processingId.set(null);
      }
    });
  } 
}
