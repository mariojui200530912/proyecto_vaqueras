import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { JuegoComponent } from './pages/juego/juego.component';
import { RegistroComponent } from './pages/auth/registro/registro.component';
import { AdminDashboard } from './pages/admin/adminDashboard/admin-dashboard';
import { adminGuard } from './guards/auth/admin-guard';
import { ReporteComponent } from './pages/reportes/reporte.component';
import { GestionCategorias } from './pages/admin/categorias/gestion-categorias';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },
    { path: 'juego', component: JuegoComponent },
    {
        path: 'admin/dashboard',
        component: AdminDashboard,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/categorias', 
        component: GestionCategorias,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/reportes',
        component: ReporteComponent,
        canActivate: [adminGuard]
    },
    {
        path: 'empresa/reportes',
        component: ReporteComponent,
        // canActivate: [empresaGuard] <--- Crear guard empresa
    },
    {
        path: 'mi-perfil/reportes',
        component: ReporteComponent,
        // canActivate: [authGuard]
    }
];
