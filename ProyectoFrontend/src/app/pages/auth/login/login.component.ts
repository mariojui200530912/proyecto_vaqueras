import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { LoginRequest } from '../../../models/auth/LoginRequest';

@Component({
  selector: 'app-login.component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  // Datos del formulario
  loginData: LoginRequest = {
    email: '',
    password: ''
  };

  errorMessage: string = '';
  isLoading: boolean = false;

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.loginData).subscribe({
      next: (user) => {
        console.log('Login exitoso:', user);
        // Redireccionar según el rol
        this.authService.redirigirSegunRol(user);
      },
      error: (err) => {
        console.error('Error login:', err);
        this.isLoading = false;
        // Mostramos el mensaje que venga del backend o uno genérico
        this.errorMessage = err.error?.mensaje || 'Credenciales incorrectas o error en el servidor.';
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}
