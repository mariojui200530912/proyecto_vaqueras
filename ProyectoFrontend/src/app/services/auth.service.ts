import { inject, Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Usuario } from '../models/usuario/Usuario';
import { LoginRequest } from '../models/auth/LoginRequest';
import { RegistroRequest } from '../models/auth/RegistroRequest';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = `${environment.apiUrl}/usuario`;

  // 1. SIGNAL: Almacena el usuario actual. Inicialmente busca en localStorage.
  currentUser = signal<Usuario | null>(this.obtenerUsuarioDelStorage());

  // 2. SIGNAL COMPUTADA: Nos dice true/false si está logueado automáticamente
  isLoggedIn = computed(() => !!this.currentUser());

  // 3. SIGNAL COMPUTADA: Para saber rol del usuario actual
  isAdmin = computed(() => this.currentUser()?.rol === 'ADMIN');
  isEmpresa = computed(() => this.currentUser()?.rol === 'EMPRESA');
  isGamer = computed(() => this.currentUser()?.rol === 'GAMER');

  login(credenciales: LoginRequest) {
    return this.http.post<Usuario>(`${this.apiUrl}/login`, credenciales).pipe(
      tap((usuarioRecibido) => {
        // A. Guardar en Signals (Memoria)
        this.currentUser.set(usuarioRecibido);

        // B. Guardar en LocalStorage (Persistencia si recarga página)
        localStorage.setItem('usuario', JSON.stringify(usuarioRecibido));
      })
    );
  }

  logout() {
    localStorage.removeItem('usuario');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  registro(datos: FormData) {
    return this.http.post<Usuario>(`${this.apiUrl}/registro`, datos);
  }

  public redirigirSegunRol(usuario: Usuario): void {
    let rutaDestino = '/';
    // A. Guardar en Signals
    this.currentUser.set(usuario);
    // B. Guardar en LocalStorage
    localStorage.setItem('usuario', JSON.stringify(usuario));
    switch (usuario.rol) {
      case 'ADMIN':
        rutaDestino = '/admin/dashboard';
        break;
      case 'EMPRESA':
        rutaDestino = '/empresa/dashboard';
        break;
      case 'GAMER':
      default:
        rutaDestino = '/'; // Home
        break;
    }
    this.router.navigate([rutaDestino]);
  }

  // Método auxiliar para leer del localStorage al iniciar la app
  private obtenerUsuarioDelStorage(): Usuario | null {
    const userStr = localStorage.getItem('usuario');
    return userStr ? JSON.parse(userStr) : null;
  }
  // Verifica si el usuario actual es dueño de la empresa creadora del juego
  esDuenio(idEmpresaCreadoraJuego: number | undefined): boolean {
    const user = this.currentUser();
    
    if (user && user.rol === 'EMPRESA' && user.idEmpresa) {
        return user.idEmpresa === idEmpresaCreadoraJuego;
    }
    
    return false;
  }
}
