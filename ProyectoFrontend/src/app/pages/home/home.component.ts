import { Component, signal, computed } from '@angular/core';
import { OnInit, inject } from '@angular/core';
import { JuegoService } from '../../services/juego.service';
import { CurrencyPipe } from '@angular/common';
import { RouterLink, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { UsuarioService } from '../../services/usuario.service';
import { Juego } from '../../models/juego/Juego';
import { FormsModule } from '@angular/forms';
import { EmpresaService } from '../../services/empresa.service';

@Component({
  selector: 'app-home.component',
  imports: [CurrencyPipe, RouterModule, RouterLink, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  public juegoService = inject(JuegoService);
  public cartService = inject(CartService);
  public usuarioService = inject(UsuarioService);
  public empresaService = inject(EmpresaService);

  searchTerm = signal<string>('');
  searchEmpresa = signal<string>('');

  usuariosFiltrados = computed(() => {
    const termino = this.searchTerm().toLowerCase();
    const usuarios = this.usuarioService.usuariosGamer(); // Asumiendo que tienes esta signal en el servicio

    if (!termino) return usuarios;

    return usuarios.filter(u => 
      u.nickname.toLowerCase().includes(termino) || 
      (u.pais && u.pais.toLowerCase().includes(termino))
    );
  });

  empresasFiltradas = computed(() => {
    const termino = this.searchEmpresa().toLowerCase();
    const empresas = this.empresaService.empresas(); // Asumiendo que llenamos esta signal

    if (!termino) return empresas;

    return empresas.filter(e => 
      e.nombre.toLowerCase().includes(termino) || 
      (e.descripcion && e.descripcion.toLowerCase().includes(termino))
    );
  });

  ngOnInit(): void {
    // Al cargar la página, pedimos los datos
    this.juegoService.cargarInicio();
    this.usuarioService.cargarUsuariosGamer();
    this.empresaService.cargarEmpresas();
  }

  agregarAlCarrito(juego: Juego) {
    const agregado = this.cartService.agregar(juego);
    if (agregado) {
      // Opcional: Mostrar un Toast o alerta pequeña
      alert('Juego agregado al carrito');
    } else {
      alert('Este juego ya está en tu carrito');
    }
  }
}
