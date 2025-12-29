import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { inject, signal } from '@angular/core';
import { OnInit, Input } from '@angular/core';
import { EmpresaService } from '../../../services/empresa.service';
import { UsuarioService } from '../../../services/usuario.service';
import { AuthService } from '../../../services/auth.service';
import { Usuario } from '../../../models/usuario/Usuario';

@Component({
  selector: 'app-usuarios-empresa',
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios-empresa.component.html',
  styleUrl: './usuarios-empresa.component.css',
})
export class UsuariosEmpresaComponent implements OnInit {
  private empresaService = inject(EmpresaService);
  private usuarioService = inject(UsuarioService); // Para buscar candidatos
  public authService = inject(AuthService);

  @Input({ required: true }) idEmpresa!: number;

  // Estado
  empleados = signal<any[]>([]);
  candidatos = signal<Usuario[]>([]);
  textoBusqueda = '';
  rolSeleccionado = 'EMPLEADO'; // Por defecto

  ngOnInit() {
    this.cargarEmpleados();
    this.usuarioService.cargarUsuariosGamer();
  }

  cargarEmpleados() {
    this.empresaService.listarEmpleados(this.idEmpresa).subscribe({
      next: (data) => this.empleados.set(data),
      error: (e) => console.error(e)
    });
  }

  // BUSCAR USUARIOS PARA AGREGAR
  buscarCandidatos() {
    if (this.textoBusqueda.length < 3) return;
    const todos = this.usuarioService.usuariosGamer(); 
    const termino = this.textoBusqueda.toLowerCase();

    const filtrados = todos.filter(u => 
      (u.nickname.toLowerCase().includes(termino) || u.email.toLowerCase().includes(termino)) &&
      u.rol !== 'ADMIN' // No vincular al super admin global a una empresa
    );
    
    this.candidatos.set(filtrados);
  }

  // ACCIÓN DE VINCULAR
  vincular(usuario: Usuario) {
    const rolFinal = this.authService.isAdmin() ? this.rolSeleccionado : 'EMPLEADO';

    if(!confirm(`¿Vincular a ${usuario.nickname} con el rol ${rolFinal}?`)) return;

    this.empresaService.vincularUsuario(this.idEmpresa, usuario.id, rolFinal).subscribe({
      next: () => {
        alert('Usuario vinculado correctamente');
        this.cargarEmpleados(); // Recargar lista
        this.candidatos.set([]); // Limpiar búsqueda
        this.textoBusqueda = '';
      },
      error: (err) => alert('Error: ' + (err.error?.mensaje))
    });
  }

  desvincular(idUsuario: number) {
    if(!confirm('¿Desvincular a este usuario de la empresa?')) return;

    this.empresaService.desvincularUsuario(idUsuario).subscribe({
      next: () => this.cargarEmpleados(),
      error: (e) => alert('Error: ' + e.error?.mensaje)
    });
  }
}
