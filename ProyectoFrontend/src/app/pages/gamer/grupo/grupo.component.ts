import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { GrupoService } from '../../../services/grupo.service';
import { AuthService } from '../../../services/auth.service';
import { signal, inject, OnInit } from '@angular/core';
import { GrupoResponse } from '../../../models/grupo/GrupoResponse';
import { Usuario } from '../../../models/usuario/Usuario';
import { JuegoGrupoResponse } from '../../../models/grupo/JuegoGrupoResponse';
import { PrestamoService } from '../../../services/prestamo.service';

@Component({
  selector: 'app-grupo.component',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './grupo.component.html',
  styleUrl: './grupo.component.css',
})
export class GrupoComponent implements OnInit {
  public grupoService = inject(GrupoService);
  public authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private prestamoService = inject(PrestamoService);

  isLoading = signal<boolean>(false);
  juegosGrupo = signal<JuegoGrupoResponse[]>([]);
  isLoadingJuegos = signal<boolean>(false);
  
  // Formulario para crear grupo
  crearForm = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]]
  });

  // Input simple para agregar ID de usuario (En un sistema real sería un buscador)
  idNuevoMiembro: number | null = null;

  ngOnInit() {
    this.cargarGrupoDelServidor();
  }

  cargarGrupoDelServidor() {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;

    this.isLoading.set(true);

    this.grupoService.obtenerGrupoPorUsuario(userId).subscribe({
      next: (grupo) => {
        if (grupo) {
             this.cargarJuegosDelGrupo(grupo.id);
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error verificando grupo', err);
        this.isLoading.set(false);
      }
    });
  }

  // --- ACCIONES ---

  crearGrupo() {
    if (this.crearForm.invalid) return;
    
    const nombre = this.crearForm.value.nombre!;
    const userId = this.authService.currentUser()?.id!;

    this.isLoading.set(true);
    this.grupoService.crearGrupo(userId, nombre).subscribe({
      next: (grupo) => {
           this.isLoading.set(false);
           alert(`¡Grupo "${grupo.nombre}" creado!`);
        },
      error: (e) => {
        this.isLoading.set(false);
        alert('Error al crear: ' + e.message);
      }
    });
  }

  agregarMiembro() {
    if (!this.idNuevoMiembro) return;
    const grupo = this.grupoService.miGrupo();
    if (!grupo) return;

    // Validación básica: no agregarse a uno mismo
    if (this.idNuevoMiembro === this.authService.currentUser()?.id) {
        alert("Ya eres parte del grupo.");
        return;
    }

    this.isLoading.set(true);
    this.grupoService.agregarMiembro(grupo.id, this.idNuevoMiembro).subscribe({
      next: (res) => {
        alert(res.mensaje);
        this.idNuevoMiembro = null; // Reset input
        this.isLoading.set(false);
      },
      error: (e) => {
        alert('Error: ' + e.error?.mensaje || e.message);
        this.isLoading.set(false);
      }
    });
  }

  // Eliminar a otro (Kick) o Salirse (Leave)
  eliminarUsuario(miembro: Usuario) {
    const grupo = this.grupoService.miGrupo();
    const yo = this.authService.currentUser();
    if (!grupo || !yo) return;

    const esMiPropioUsuario = miembro.id === yo.id;
    const soyLider = grupo.idCreador === yo.id;

    // Confirmación
    const mensaje = esMiPropioUsuario 
      ? '¿Seguro que quieres salir del grupo?' 
      : `¿Expulsar a ${miembro.nickname}?`;

    if (!confirm(mensaje)) return;

    this.isLoading.set(true);
    this.grupoService.eliminarMiembro(grupo.id, miembro.id, yo.id).subscribe({
      next: (res) => {
        alert(res.mensaje);
        if (esMiPropioUsuario) {
          localStorage.removeItem('idGrupoUsuario');
        }
        this.isLoading.set(false);
      },
      error: (e) => {
        alert('Error: ' + e.message);
        this.isLoading.set(false);
      }
    });
  }

  eliminarGrupo() {
    const grupo = this.grupoService.miGrupo();
    const yo = this.authService.currentUser();
    if (!grupo || !yo) return;

    if (!confirm('¿ELIMINAR GRUPO? Esto borrará el grupo para todos los miembros.')) return;

    this.isLoading.set(true);
    this.grupoService.eliminarGrupo(grupo.id, yo.id).subscribe({
      next: (res) => {
        alert(res.mensaje);
        localStorage.removeItem('idGrupoUsuario');
        this.isLoading.set(false);
      },
      error: (e) => {
        alert('Error: ' + e.message);
        this.isLoading.set(false);
      }
    });
  }

  esLider(): boolean {
    return this.grupoService.miGrupo()?.idCreador === this.authService.currentUser()?.id;
  }

  cargarJuegosDelGrupo(idGrupo: number) {
    const myId = this.authService.currentUser()?.id;
    if (!myId) return;

    this.isLoadingJuegos.set(true);
    this.grupoService.obtenerJuegosGrupo(idGrupo, myId).subscribe({
      next: (data) => {
        this.juegosGrupo.set(data);
        this.isLoadingJuegos.set(false);
      },
      error: () => this.isLoadingJuegos.set(false)
    });
  }

  solicitarJuego(juego: JuegoGrupoResponse) {
    const myId = this.authService.currentUser()?.id;
    if (!myId) return;

    if (!confirm(`¿Solicitar préstamo de "${juego.titulo}" a ${juego.nombreDueno}?`)) return;

    // Llamamos al PrestamoService que ya tenías creado
    // request: { idJuego, idDueno }
    const request = { idJuego: juego.idJuego, idDueno: juego.idDueno };
    
    this.prestamoService.solicitarPrestamo(myId, request).subscribe({
      next: (res) => {
        alert('¡Solicitud exitosa! El juego ahora aparece en tu Biblioteca (Sección Prestados).');
        
        this.juegosGrupo.update(list => list.filter(j => j.idJuego !== juego.idJuego));
      },
      error: (e) => alert('Error al solicitar: ' + e.error?.message)
    });
  }

}
