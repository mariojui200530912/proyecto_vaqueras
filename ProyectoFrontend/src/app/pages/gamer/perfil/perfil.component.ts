import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { UsuarioService } from '../../../services/usuario.service';
import { BibliotecaService } from '../../../services/biblioteca.service';
import { AuthService } from '../../../services/auth.service';
import { Usuario } from '../../../models/usuario/Usuario';
import { Biblioteca } from '../../../models/biblioteca/Biblioteca';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-perfil.component',
  imports: [CommonModule, RouterLink],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css',
})
export class PerfilComponent implements OnInit{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private usuarioService = inject(UsuarioService);
  public bibliotecaService = inject(BibliotecaService);
  public authService = inject(AuthService);

  // Estados
  perfilUsuario = signal<Usuario | null>(null);
  biblioteca = signal<Biblioteca[]>([]);
  
  isLoading = signal<boolean>(true);
  isError = signal<boolean>(false);
  mensajeError = signal<string>('');
  
  // Estado de Privacidad
  bibliotecaEsPublica = signal<boolean>(true);
  esMiPerfil = signal<boolean>(false);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      
      // Si hay ID en la URL, buscamos ese usuario. Si no, buscamos el logueado.
      const targetId = idParam ? Number(idParam) : this.authService.currentUser()?.id;

      if (targetId) {
        this.cargarPerfil(targetId);
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  cargarPerfil(id: number) {
    this.isLoading.set(true);
    const myId = this.authService.currentUser()?.id;
    this.esMiPerfil.set(id === myId);

    // Obtener Datos del Usuario (Avatar, Pais, Nickname)
    this.usuarioService.obtenerUsuarioPorId(id).subscribe({
      next: (user) => {
        this.perfilUsuario.set(user);
        this.bibliotecaEsPublica.set(user.bibliotecaPublica!); 
        
        // Intentar obtener la biblioteca
        this.bibliotecaService.cargarBiblioteca(id);
        this.isLoading.set(false);
      },
      error: (e) => {
        this.isError.set(true);
        this.mensajeError.set('Usuario no encontrado');
        this.isLoading.set(false);
      }
    });
  }

  togglePrivacidad() {
    const user = this.perfilUsuario();
    if (!user) return;

    // Invertimos el valor actual para enviar al backend
    const nuevoEstado = !this.bibliotecaEsPublica();

    // Optimistic Update: Actualizamos la UI inmediatamente para que se sienta rápido
    this.bibliotecaEsPublica.set(nuevoEstado);

    this.bibliotecaService.cambiarVisibilidad(user.id, nuevoEstado).subscribe({
      next: (res) => {
        // Todo bien, mostramos mensaje opcional
        // alert(res.mensaje); 
      },
      error: (e) => {
        // Si falla, revertimos el cambio visual
        this.bibliotecaEsPublica.set(!nuevoEstado);
        alert('Error al cambiar privacidad: ' + e.message);
      }
    });
  }
}
