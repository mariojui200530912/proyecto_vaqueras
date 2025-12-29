import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { RegistroComponent } from './pages/auth/registro/registro.component';
import { AdminDashboard } from './pages/admin/adminDashboard/admin-dashboard';
import { adminGuard } from './guards/auth/admin-guard';
import { ReporteComponent } from './pages/reportes/reporte.component';
import { CategoriasComponent } from './pages/admin/categorias/categorias.component';
import { DetalleJuegoComponent } from './pages/juego/detalle-juego/detalle-juego.component';
import { BannerComponent } from './pages/admin/banner/banner.component';
import { GestionComisionComponent } from './pages/admin/comision/gestion-comision.component';
import { GestionEmpresaComponent } from './pages/admin/empresas/gestion-empresa.component';
import { GestionUsuariosComponent } from './pages/admin/usuarios/gestion-usuarios.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },
    {
        path: 'juego/:id',
        component: DetalleJuegoComponent
    },
    {
        path: 'admin/dashboard',
        component: AdminDashboard,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/categorias',
        component: CategoriasComponent,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/banner',
        component: BannerComponent,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/comision',
        component: GestionComisionComponent,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/usuarios',
        component: GestionUsuariosComponent,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/empresas',
        component: GestionEmpresaComponent,
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
