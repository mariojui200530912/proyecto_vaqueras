import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

// 1. GUARD AUTENTICADO: Solo deja pasar si está logueado
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

// 2. GUARD PÚBLICO: Solo deja pasar si NO está logueado (Para el Login/Registro)
export const publicGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return true;
  }
  // Si ya está logueado, redirigir según su rol
  const user = authService.currentUser();
  if (user?.rol === 'ADMIN') router.navigate(['/admin/dashboard']);
  else if (user?.rol === 'EMPRESA') router.navigate([`/empresa/${user.idEmpresa}`]);
  else router.navigate(['/']);
  
  return false;
};

// 3. GUARD ADMIN: Solo deja pasar si es ADMIN
export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Primero verificamos si está logueado
  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  // Luego verificamos si es Admin usando tu Signal computada
  if (authService.isAdmin()) {
    return true;
  }

  // Si está logueado pero no es admin, lo mandamos al home o página de error
  alert('Acceso denegado. Se requieren permisos de Administrador.');
  router.navigate(['/']);
  return false;
};

// 4. GUARD EMPRESA: Solo deja pasar si es EMPRESA
export const empresaGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  if (authService.isEmpresa()) {
    return true;
  }

  alert('Acceso denegado. Área exclusiva para empresas.');
  router.navigate(['/']);
  return false;
};