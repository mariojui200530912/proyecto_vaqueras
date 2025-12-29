import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Usuario } from '../../../models/usuario/Usuario';
import { UsuarioService } from '../../../services/usuario.service';
import { inject } from '@angular/core';

@Component({
  selector: 'app-gestion-usuarios.component',
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-usuarios.component.html',
  styleUrl: './gestion-usuarios.component.css',
})
export class GestionUsuariosComponent {
  public usuarioService = inject(UsuarioService);

  // Estado del componente
  modoEdicion = false;
  archivoSeleccionado: File | null = null;
  textoBusqueda = '';

  // Modelo del formulario
  formUsuario: any = {
    nickname: '',
    email: '',
    password: '', // Solo para creación
    fechaNacimiento: '',
    telefono: '',
    pais: '',
    rol: '',
    estado: 'ACTIVO'
  };

  // --- BUSCADOR INTELIGENTE ---
  get usuariosFiltrados() {
    const lista = this.usuarioService.usuarios();
    const termino = this.textoBusqueda.toLowerCase().trim();

    if (!termino) return lista;

    return lista.filter(u => 
      u.nickname.toLowerCase().includes(termino) || 
      u.email.toLowerCase().includes(termino)
    );
  }

  // --- GESTIÓN DEL FORMULARIO ---
  iniciarCreacion() {
    this.modoEdicion = false;
    this.resetForm();
  }

  cargarParaEditar(usuario: Usuario) {
    this.modoEdicion = true;
    // Copiamos los datos. Importante: Password no se carga, se deja vacío o null
    this.formUsuario = { ...usuario, password: '' };
    this.archivoSeleccionado = null;
  }

  resetForm() {
    this.formUsuario = {
      nickname: '', email: '', password: '', fechaNacimiento: '',
      telefono: '', pais: '', rol: '', estado: 'ACTIVO'
    };
    this.archivoSeleccionado = null;
  }

  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.archivoSeleccionado = event.target.files[0];
    }
  }

  guardar() {
    if (!this.formUsuario.nickname || !this.formUsuario.email) {
      alert('Nickname y Email son obligatorios');
      return;
    }

    if (this.modoEdicion && this.formUsuario.id) {
      // ACTUALIZAR
      this.usuarioService.actualizar(this.formUsuario.id, this.formUsuario, this.archivoSeleccionado)
        .subscribe({
          next: () => { alert('Usuario actualizado'); this.iniciarCreacion(); },
          error: (e) => alert('Error: ' + e.error?.mensaje)
        });
    } else {
      // CREAR
      if (!this.formUsuario.password) {
        alert('La contraseña es obligatoria para nuevos usuarios');
        return;
      }
      this.usuarioService.registrar(this.formUsuario, this.archivoSeleccionado)
        .subscribe({
          next: () => { alert('Usuario registrado'); this.iniciarCreacion(); },
          error: (e) => alert('Error: ' + e.error?.mensaje)
        });
    }
  }

  // --- ACCIONES DE TABLA ---
  eliminar(id: number) {
    if(confirm('¿Estás seguro de eliminar este usuario?')) {
      this.usuarioService.eliminar(id).subscribe();
    }
  }

  toggleEstado(usuario: Usuario) {
    const nuevoEstado = usuario.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    this.usuarioService.cambiarEstado(usuario.id, nuevoEstado).subscribe({
      next: () => {}, // El signal actualiza la UI
      error: (e) => alert('Error al cambiar estado: ' + e.error?.mensaje)
    });
  }
}
