import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ActivatedRoute, Router } from '@angular/router';
import { inject, signal } from '@angular/core';
import { EmpresaService } from '../../../services/empresa.service';
import { JuegoService } from '../../../services/juego.service';
import { AuthService } from '../../../services/auth.service';
import { Empresa } from '../../../models/empresa/Empresa';
import { Juego } from '../../../models/juego/Juego';

@Component({
  selector: 'app-perfil-empresa.component',
  imports: [CommonModule, RouterModule],
  templateUrl: './perfil-empresa.component.html',
  styleUrl: './perfil-empresa.component.css',
})
export class PerfilEmpresaComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private empresaService = inject(EmpresaService);
  private juegoService = inject(JuegoService);
  public authService = inject(AuthService);

  // Estados
  empresa = signal<Empresa | null>(null);
  juegos = signal<Juego[]>([]);
  isLoading = signal<boolean>(true);

  // ID de la empresa actual
  empresaId!: number;

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) {
        this.empresaId = id;
        this.cargarDatos();
      }
    });
  }

  // GETTER: ¿Soy el dueño de este perfil?
  get esPropietario(): boolean {
    const user = this.authService.currentUser();
    // Verifica si el usuario logueado tiene el mismo ID de empresa que el perfil que visita
    return user?.rol === 'EMPRESA' && user.idEmpresa === this.empresaId;
  }

  cargarDatos() {
    this.isLoading.set(true);
    
    // 1. Cargar Info Empresa
    this.empresaService.obtenerPorId(this.empresaId).subscribe({
      next: (data) => {
        this.empresa.set(data);
        
        // 2. Cargar Catálogo de Juegos (Pasamos esPropietario para ver suspendidos)
        this.cargarJuegos();
      },
      error: () => {
        this.isLoading.set(false);
        alert('Empresa no encontrada');
        this.router.navigate(['/']);
      }
    });
  }

  cargarJuegos() {
    this.juegoService.obtenerJuegosPorEmpresa(this.empresaId, this.esPropietario)
      .subscribe({
        next: (data) => {
          this.juegos.set(data);
          this.isLoading.set(false);
        }
      });
  }

  // --- GESTIÓN DE CATÁLOGO ---

  toggleEstado(juego: Juego) {
    const nuevoEstado = juego.estadoVenta === 'ACTIVO' ? 'SUSPENDIDO' : 'ACTIVO';
    const accion = nuevoEstado === 'SUSPENDIDO' ? 'suspender la venta de' : 'reactivar la venta de';

    if(!confirm(`¿Deseas ${accion} "${juego.titulo}"?`)) return;

    this.juegoService.cambiarEstadoJuego(juego.id, nuevoEstado).subscribe({
      next: () => {
        this.cargarJuegos(); 
      },
      error: (e) => alert('Error: ' + e.error?.mensaje)
    });
  }

  irACrearJuego() {
    // Redirigir a tu formulario de creación de juegos
    this.router.navigate(['/empresa/juego/nuevo']); 
  }

  irAEditarJuego(idJuego: number) {
     // Redirigir al detalle en modo edición o a un form específico
     this.router.navigate(['/juego', idJuego]); 
  }
  
  toggleComentariosGlobal() {
    const emp = this.empresa();
    if (!emp || !this.esPropietario) return;

    const nuevoEstado = !emp.permiteComentarios; // Invertimos el estado actual
    
    // Feedback optimista (cambiamos visualmente antes de la respuesta)
    this.empresa.update(current => current ? { ...current, permiteComentarios: nuevoEstado } : null);

    this.empresaService.configurarComentarios(emp.id, nuevoEstado).subscribe({
      next: (res: any) => {
        // Opcional: Mostrar un toast o mensaje pequeño
        // alert(res.mensaje);
      },
      error: (e) => {
        console.error(e);
        alert('Error al cambiar configuración: ' + (e.error?.mensaje || e.message));
        // Si falla, revertimos el cambio visualmente
        this.empresa.update(current => current ? { ...current, permiteComentarios: !nuevoEstado } : null);
      }
    });
  }
}
