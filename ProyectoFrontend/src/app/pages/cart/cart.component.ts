import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { inject, signal } from '@angular/core';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart.component',
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
})
export class CartComponent {
  public cartService = inject(CartService);
  public authService = inject(AuthService);
  private router = inject(Router);

  // Estados locales para UI
  isProcessing = signal<boolean>(false);
  mensajeExito = signal<string>('');
  mensajeError = signal<string>('');

  getImagen(base64: string | undefined): string {
    if (!base64) return 'assets/images/placeholder.jpg';
    return base64.startsWith('data:') ? base64 : `data:image/jpeg;base64,${base64}`;
  }

  realizarPago() {
    const user = this.authService.currentUser();

    if (!this.authService.isLoggedIn()) {
      alert('Debes iniciar sesión para comprar.');
      this.router.navigate(['/login']);
      return;
    }

    if (user?.carteraSaldo! < this.cartService.total()) {
      this.mensajeError.set('Saldo insuficiente. Por favor recarga tu billetera.');
      return;
    }

    this.isProcessing.set(true);
    this.mensajeError.set('');
    
    this.cartService.procesarCompra(user?.id!).subscribe({
      next: (respuestas) => {
        // Calcular nuevo saldo estimado para actualizar
        const nuevoSaldo = user?.carteraSaldo! - this.cartService.total(); 
        this.authService.actualizarUsuario(user?.id!);
        this.mensajeExito.set(`¡Compra realizada! Has adquirido ${respuestas.length} juegos.`);
        this.isProcessing.set(false);
      },
      error: (err) => {
        console.error(err);
        this.mensajeError.set('Ocurrió un error al procesar la compra. Es posible que ya tengas alguno de estos juegos.');
        this.isProcessing.set(false);
      }
    });
  }
}
