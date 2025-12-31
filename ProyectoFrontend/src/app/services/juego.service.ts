import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Juego } from '../models/juego/Juego';
import { Banner } from '../models/banner/Banner';
import { map } from 'rxjs/operators';
import { Categoria } from '../models/categoria/Categoria';
import { JuegoCreacion } from '../models/juego/JuegoCreacion';

@Injectable({
  providedIn: 'root',
})
export class JuegoService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  // Signals para almacenar los datos
  bannerData = signal<Banner[]>([]);
  juegosCatalogo = signal<Juego[]>([]);
  juegosDestacados = signal<Juego[]>([]);
  isLoading = signal<boolean>(true);

  publicarJuego(idUsuario: number, datosJuego: JuegoCreacion, portada: File, galeria: File[]) {
    const formData = new FormData();
    formData.append('id_usuario', idUsuario.toString());
    formData.append('datos', JSON.stringify(datosJuego));

    if (portada) {
      formData.append('portada', portada);
    }

    if (galeria && galeria.length > 0) {
      galeria.forEach(archivo => {
        formData.append('galeria', archivo);
      });
    }

    return this.http.post(`${this.apiUrl}/juego`, formData);
  }
  
  obtenerBanner() {
    return this.http.get<Banner[]>(`${this.apiUrl}/banner`);
  }
  
  obtenerJuegoPorId(id: number) {
    return this.http.get<Juego>(`${this.apiUrl}/juego/${id}`);
  }

  obtenerJuegos() {
    return this.http.get<Juego[]>(`${this.apiUrl}/juego/catalogo`);
  }

  obtenerDestacados() {
    return this.http.get<Juego[]>(`${this.apiUrl}/juego/destacados`);
  }

  obtenerJuegosPorEmpresa(idEmpresa: number, esDuenio: boolean) {
    let params = new HttpParams();
    if (esDuenio) {
      params = params.set('rol', 'EMPRESA'); // Esto hace que el backend devuelva también los SUSPENDIDOS
    }
    return this.http.get<Juego[]>(`${this.apiUrl}/juego/catalogo/${idEmpresa}/empresa`, { params });
  }

  buscarJuegos(titulo?: string, idCategoria?: number, min?: number, max?: number) {
    let params = new HttpParams();
    if (titulo) params = params.set('titulo', titulo);
    if (idCategoria) params = params.set('categoria', idCategoria);
    if (min) params = params.set('min', min);
    if (max) params = params.set('max', max);

    return this.http.get<Juego[]>(`${this.apiUrl}/juego/buscar`, { params });
  }

  actualizarDatosJuego(idJuego: number, idUsuario: number, datos: any) {
    const formData = new FormData();
    formData.append('idUsuario', idUsuario.toString());
    formData.append('datos', JSON.stringify(datos));

    return this.http.post(`${this.apiUrl}/${idJuego}/actualizar`, formData);
  }

  actualizarPortada(idJuego: number, archivo: File) {
    const formData = new FormData();
    formData.append('portada', archivo);
    return this.http.put(`${this.apiUrl}/juego/${idJuego}/actualizar/imagen`, formData);
  }

  obtenerCategoriasDeJuego(idJuego: number) {
    return this.http.get<Categoria[]>(`${this.apiUrl}/juego/${idJuego}/categorias`);
  }
  // Agregar una categoría a un juego
  agregarCategoria(idJuego: number, idCategoria: number) {
    return this.http.post(`${this.apiUrl}/juego/${idJuego}/categoria/${idCategoria}`, {});
  }
  // Elimina una categoría de un juego
  eliminarCategoria(idJuego: number, idCategoria: number) {
    const params = new HttpParams().set('idCategoria', idCategoria);
    return this.http.delete(`${this.apiUrl}/juego/${idJuego}/categoria`, { params });
  }
  // SUBIR IMÁGENES A LA GALERÍA
  agregarImagenesGaleria(idJuego: number, archivos: File[]) {
    const formData = new FormData();
    
    archivos.forEach(file => {
      formData.append('imagenes', file);
    });

    return this.http.post(`${this.apiUrl}/juego/${idJuego}/galeria`, formData);
  }
  // ELIMINAR UNA IMAGEN DE LA GALERÍA
  eliminarImagenGaleria(idJuego: number, idImagen: number) {
    return this.http.delete(`${this.apiUrl}/juego/${idJuego}/galeria/${idImagen}`);
  }
  // SUBIR ACTUALIZAR BANNER ESPECÍFICO
  subirBanner(idJuego: number, archivo: File) {
    const formData = new FormData();
    formData.append('imagen', archivo);
    return this.http.put(`${this.apiUrl}/juego/${idJuego}/imagenes/banner`, formData);
  }

  cambiarEstadoJuego(idJuego: number, nuevoEstado: 'ACTIVO' | 'SUSPENDIDO') {
    const body = { estadoVenta: nuevoEstado };
    return this.http.patch(`${this.apiUrl}/juego/${idJuego}/estado`, body);
  }

  configurarComentarios(idJuego: number, permitir: boolean) {
    const formData = new FormData();
    formData.append('permitir', permitir.toString()); 
    
    return this.http.patch(`${this.apiUrl}/juego/${idJuego}/configuracion/comentarios`, formData);
  }

  // Método maestro para cargar todo al inicio
  cargarInicio() {
    this.isLoading.set(true);

    this.obtenerBanner().subscribe({
      next: (data) => this.bannerData.set(data),
      error: (e) => console.error('Error banner', e)
    });

    this.obtenerJuegos().subscribe({
      next: (data) => {
        this.juegosCatalogo.set(data);
        this.isLoading.set(false);
      },
      error: (e) => {
        console.error('Error juegos', e);
        this.isLoading.set(false);
      }
    });

    this.obtenerDestacados().subscribe({
      next: (data) => this.juegosDestacados.set(data)
    });
  }
}
