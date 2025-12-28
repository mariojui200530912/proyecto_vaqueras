import { Injectable } from '@angular/core';
import { inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { DashboardStats } from '../models/admin/DashboardStats';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  // Signal para almacenar las stats
  stats = signal<DashboardStats | null>(null);

  cargarStats() {
    this.http.get<DashboardStats>(`${this.apiUrl}/admin/stats`)
      .subscribe(data => this.stats.set(data));
  }
 
}
