import { Component } from '@angular/core';
import { OnInit, inject } from '@angular/core';
import { JuegoService } from '../../services/juego.service';
import { CurrencyPipe } from '@angular/common';
import { RouterLink, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { Juego } from '../../models/juego/Juego';

@Component({
  selector: 'app-home.component',
  imports: [CurrencyPipe, RouterModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  public juegoService = inject(JuegoService);
  public cartService = inject(CartService);

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

  agregarAlCarrito(juego: Juego) {
    const agregado = this.cartService.agregar(juego);
    if (agregado) {
      // Opcional: Mostrar un Toast o alerta pequeña
      alert('Juego agregado al carrito');
    } else {
      alert('Este juego ya está en tu carrito');
    }
  }
}
