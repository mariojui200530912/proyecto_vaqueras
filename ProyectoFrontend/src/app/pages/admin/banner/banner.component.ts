import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { inject } from '@angular/core';
import { BannerService } from '../../../services/banner.service';
import { JuegoService } from '../../../services/juego.service';
import { BuscadorComponent } from '../../../shared/components/buscador/buscador.component';

@Component({
  selector: 'app-banner.component',
  imports: [CommonModule, FormsModule, BuscadorComponent],
  templateUrl: './banner.component.html',
  styleUrl: './banner.component.css',
})
export class BannerComponent {
  public bannerService = inject(BannerService);
  private juegoService = inject(JuegoService);

  // Para buscar juego a agregar
  busquedaId: number | null = null;
  juegoEncontrado: any = null; // Guardar info temporal del juego buscado

  agregarAlBanner() {
    if (!this.juegoEncontrado) return;
    
    this.bannerService.agregarAlBanner(this.juegoEncontrado.id).subscribe({
      next: () => {
        alert('Juego agregado al banner');
        this.juegoEncontrado = null;
        this.busquedaId = null;
      },
      error: (e) => alert('Error: ' + e.error?.mensaje)
    });
  }

  // --- LÓGICA DE GESTIÓN ---
  eliminar(idJuego: number) {
    if (!confirm('¿Quitar del banner principal?')) return;
    this.bannerService.quitarDelBanner(idJuego).subscribe();
  }

  // Mover elemento arriba o abajo
  cambiarPosicion(item: any, direccion: 'subir' | 'bajar') {
    const currentOrder = item.orden;
    const newOrder = direccion === 'subir' ? currentOrder - 1 : currentOrder + 1;
    
    if (newOrder < 1) return; // No bajar de 1

    this.bannerService.cambiarOrden(item.idJuego, newOrder).subscribe();
  }

  onJuegoSeleccionado(juego: any) {
    if(!confirm(`¿Agregar "${juego.titulo}" al banner principal?`)) return;

    this.juegoEncontrado = juego;
    this.agregarAlBanner();
  }
}
