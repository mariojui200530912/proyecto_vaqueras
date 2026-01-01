import { Injectable } from '@angular/core';
import { inject, signal, computed, effect } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Juego } from '../models/juego/Juego';
import { VentaRequest } from '../models/venta/VentaRequest';
import { forkJoin } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/venta`; // Ajusta a tu URL base

  // Iniciamos leyendo del localStorage si existe
  private itemsSignal = signal<Juego[]>(this.cargarDelStorage());

  // Calculan automáticamente totales y cantidad
  count = computed(() => this.itemsSignal().length);
  total = computed(() => this.itemsSignal().reduce((acc, juego) => acc + (juego.precio || 0), 0));

  constructor() {
    // EFFECT: Cada vez que itemsSignal cambia, guardamos en localStorage
    effect(() => {
      localStorage.setItem('cart_items', JSON.stringify(this.itemsSignal()));
    });
  }
  // --- GESTIÓN DEL CARRITO ---
  agregar(juego: Juego) {
    const current = this.itemsSignal();
    // Evitar duplicados (juegos digitales suelen ser únicos por cuenta)
    if (!current.find(item => item.id === juego.id)) {
      this.itemsSignal.update(items => [...items, juego]);
      return true; // Agregado
    }
    return false; // Ya existía
  }

  remover(idJuego: number) {
    this.itemsSignal.update(items => items.filter(i => i.id !== idJuego));
  }

  limpiar() {
    this.itemsSignal.set([]);
  }

  obtenerItems() {
    return this.itemsSignal.asReadonly();
  }

  estaEnCarrito(idJuego: number): boolean {
    return this.itemsSignal().some(j => j.id === idJuego);
  }

  procesarCompra(idUsuario: number) {
    const items = this.itemsSignal();
    
    // Creamos una peticion por cada juego
    const peticiones = items.map(juego => {
      const request: VentaRequest = {
        idUsuario: idUsuario,
        idJuego: juego.id
      };
      return this.http.post(this.apiUrl, request);
    });

    // forkJoin ejecuta todas las peticiones en paralelo y espera a que todas terminen
    return forkJoin(peticiones).pipe(
      tap(() => {
        this.limpiar();
      })
    );
  }

  private cargarDelStorage(): Juego[] {
    const data = localStorage.getItem('cart_items');
    return data ? JSON.parse(data) : [];
  }
}
