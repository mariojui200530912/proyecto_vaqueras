import { Component } from '@angular/core';
import { OnInit, inject } from '@angular/core';
import { JuegoService } from '../../services/juego.service';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-home.component',
  imports: [CurrencyPipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  public juegoService = inject(JuegoService);

  ngOnInit(): void {
    // Al cargar la página, pedimos los datos
    this.juegoService.cargarInicio();
  }
  
  // Helper para manejar imágenes base64 si no traen el prefijo
  getImagenSrc(base64: string): string {
    if (!base64) return 'assets/images/placeholder.jpg'; // Imagen por defecto
    if (base64.startsWith('data:')) return base64;
    return `data:image/jpeg;base64,${base64}`;
  }
}
