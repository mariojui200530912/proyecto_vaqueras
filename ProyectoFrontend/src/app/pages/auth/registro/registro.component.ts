import { Component } from '@angular/core';
import { inject } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { RegistroRequest } from '../../../models/auth/RegistroRequest';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { email } from '@angular/forms/signals';

@Component({
  selector: 'app-registro.component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.css',
})
export class RegistroComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  selectedFile: File | null = null;

  // Método que se activa cuando el usuario elige una imagen
  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  // Objeto para el formulario
  datos: RegistroRequest = {
    nickname: '',
    email: '',
    password: '',
    telefono: '',
    pais: '',
    fechaNacimiento: '',
    rol: 'GAMER' // Default
  };

  confirmPassword = '';
  errorMessage = '';
  isLoading = false;

  onSubmit() {
    // Doble chequeo de seguridad (aunque el botón esté deshabilitado)
    if (this.datos.password !== this.confirmPassword) {
        this.errorMessage = "Las contraseñas no coinciden";
        return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    // Usamos FormData para enviar el archivo junto con los demás datos
    const formData = new FormData();
    formData.append('nickname', this.datos.nickname);
    formData.append('email', this.datos.email);
    formData.append('password', this.datos.password);
    formData.append('telefono', this.datos.telefono);
    formData.append('pais', this.datos.pais);
    formData.append('fechaNacimiento', this.datos.fechaNacimiento);
    formData.append('rol', this.datos.rol);

    if (this.selectedFile) {
      formData.append('avatar', this.selectedFile, this.selectedFile.name);
    }
    this.authService.registro(formData).subscribe({
      next: (user) => {
        // Éxito: Limpiamos y redirigimos
        this.isLoading = false;
        // Opcional: Mostrar un Toast o Alerta más bonita
        alert('¡Cuenta creada con éxito!'); 
        this.authService.redirigirSegunRol(user);
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        this.errorMessage = err.error?.mensaje || 'Ocurrió un error al intentar registrarte.';
      }
    });
  }

}
