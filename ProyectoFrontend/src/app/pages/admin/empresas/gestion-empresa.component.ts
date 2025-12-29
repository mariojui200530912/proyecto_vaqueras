import { CommonModule } from '@angular/common';
import { Component, inject, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Empresa } from '../../../models/empresa/Empresa';
import { EmpresaService } from '../../../services/empresa.service';
import { ConfiguracionService } from '../../../services/configuracion.service';
import { UsuariosEmpresaComponent } from "../usuarios-empresa/usuarios-empresa.component";

@Component({
  selector: 'app-gestion-empresa.component',
  imports: [CommonModule, FormsModule, UsuariosEmpresaComponent],
  templateUrl: './gestion-empresa.component.html',
  styleUrl: './gestion-empresa.component.css',
})
export class GestionEmpresaComponent {
  public empresaService = inject(EmpresaService);
  public configuracionService = inject(ConfiguracionService);

  textoBusqueda: string = '';
  get empresasFiltradas() {
    const lista = this.empresaService.empresas();
    const termino = this.textoBusqueda.toLowerCase().trim();

    // Si el buscador está vacío, retornamos la lista completa
    if (!termino) {
      return lista;
    }

    // Si hay texto, filtramos
    return lista.filter(emp => {
      // Buscamos por NOMBRE o por DESCRIPCIÓN
      const coincideNombre = emp.nombre.toLowerCase().includes(termino);
      const coincideDesc = (emp.descripcion || '').toLowerCase().includes(termino);
      
      return coincideNombre || coincideDesc;
    });
  }

  modoEdicion = false;
  archivoSeleccionado: File | null = null;
  empresaSeleccionada: Empresa | null = null;
  
  formEmpresa: Partial<Empresa> = {
    nombre: '',
    descripcion: '',
    comisionEspecifica: undefined
  };

  iniciarCreacion() {
    this.modoEdicion = false;
    this.formEmpresa = { nombre: '', descripcion: '', comisionEspecifica: 0 };
    this.archivoSeleccionado = null;
  }

  cargarParaEditar(emp: Empresa) {
    this.modoEdicion = true;
    this.formEmpresa = { ...emp };
    this.archivoSeleccionado = null;
  }

  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.archivoSeleccionado = event.target.files[0];
    }
  }

  guardar() {
    if (!this.formEmpresa.nombre) {
        alert('El nombre es obligatorio');
        return;
    }

    const datosParaEnviar = {
      nombre: this.formEmpresa.nombre,
      descripcion: this.formEmpresa.descripcion,
      comisionEspecifica: this.formEmpresa.comisionEspecifica,
      permiteComentarios: this.formEmpresa.permiteComentarios 
    };

    if (this.modoEdicion && this.formEmpresa.id) {
      // ACTUALIZAR (PUT)
      this.empresaService.actualizar(this.formEmpresa.id, datosParaEnviar, this.archivoSeleccionado)
        .subscribe({
          next: () => { 
            alert('Actualizado correctamente'); 
            this.iniciarCreacion(); 
          },
          error: (e) => {
            alert('Error al actualizar: ' + (e.error?.mensaje));
          }
        });

    } else {
      
      // CREAR (POST)
      this.empresaService.crear(datosParaEnviar, this.archivoSeleccionado)
        .subscribe({
          next: () => { 
            alert('Creado correctamente'); 
            this.iniciarCreacion(); 
          },
          error: (e) => alert('Error al crear: ' + (e.error?.mensaje || 'Error desconocido'))
        });
    }
  }

  eliminar(id: number) {
    if(confirm('¿Eliminar empresa? Se borrarán sus juegos asociados.')) {
      this.empresaService.eliminar(id).subscribe();
    }
  }

  // Acciones Rápidas
  cambiarComision(id: number, actual: number) {
    const nuevoVal = prompt('Ingrese el nuevo porcentaje:', actual.toString());
    if (nuevoVal !== null) {
        const num = parseFloat(nuevoVal);
        if (!isNaN(num) && num >= 0 && num <= 100) {
            this.empresaService.actualizarComision(id, num).subscribe({
                next: () => alert('Comisión actualizada'),
                error: (e) => alert(e.error?.mensaje)
            });
        } else {
            alert('Valor inválido');
        }
    }
  }

  toggleComentarios(emp: Empresa) {
    const nuevoEstado = !emp.permiteComentarios;
    this.empresaService.configurarComentarios(emp.id, nuevoEstado).subscribe({
        error: (e) => alert('Error: ' + e.error?.mensaje)
    });
  }

  // Modal de Usuarios Empresa
 abrirUsuariosEmpresa(emp: Empresa) {
    this.empresaSeleccionada = emp;
  }

  cerrarModalUsuarios() {
    this.empresaSeleccionada = null;
  }
}
