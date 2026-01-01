import { Component } from '@angular/core';
import { inject } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { RouterLink, RouterModule } from "@angular/router";
import { CommonModule } from '@angular/common';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  authService = inject(AuthService);
  cartService = inject(CartService);

}
