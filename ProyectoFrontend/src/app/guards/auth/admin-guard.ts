import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Verificamos el rol usando la Signal
  const user = authService.currentUser();

  if (user && user.rol === 'ADMIN') {
    return true; // Pasa
  }

  // Si no es admin, lo mandamos al inicio o login
  router.navigate(['/']);
  return false;
};
