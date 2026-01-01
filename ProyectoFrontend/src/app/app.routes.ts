import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { RegistroComponent } from './pages/auth/registro/registro.component';
import { AdminDashboard } from './pages/admin/adminDashboard/admin-dashboard';
import { ReporteComponent } from './pages/reportes/reporte.component';
import { CategoriasComponent } from './pages/admin/categorias/categorias.component';
import { DetalleJuegoComponent } from './pages/juego/detalle-juego/detalle-juego.component';
import { BannerComponent } from './pages/admin/banner/banner.component';
import { GestionComisionComponent } from './pages/admin/comision/gestion-comision.component';
import { GestionEmpresaComponent } from './pages/admin/empresas/gestion-empresa.component';
import { GestionUsuariosComponent } from './pages/admin/usuarios/gestion-usuarios.component';
import { PerfilEmpresaComponent } from './pages/empresa/perfil-empresa/perfil-empresa.component';
import { CrearJuegoComponent } from './pages/juego/crear-juego/crear-juego.component';
import { authGuard, adminGuard, empresaGuard, publicGuard } from './guards/auth/auth-guard';
import { TransaccionComponent } from './pages/transaccion/transaccion.component';
import { CartComponent } from './pages/cart/cart.component';
import { BibliotecaComponent } from './pages/gamer/biblioteca/biblioteca.component';
import { GrupoComponent } from './pages/gamer/grupo/grupo.component';
import { PerfilComponent } from './pages/gamer/perfil/perfil.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent, canActivate: [publicGuard] },
    { path: 'registro', component: RegistroComponent, canActivate: [publicGuard] },
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
        canActivate: [empresaGuard]
    },
    {
        path: 'mi-perfil/reportes',
        component: ReporteComponent,
        canActivate: [authGuard]
    },
    {
        path: 'empresa/:id',
        component: PerfilEmpresaComponent
    },
    {
        path: 'empresa/juego/nuevo',
        component: CrearJuegoComponent,
        canActivate: [empresaGuard]
    },
    {
        path: 'transaccion',
        component: TransaccionComponent,
        canActivate: [authGuard]
    },
    {
        path: 'carrito',
        component: CartComponent,
        canActivate: [authGuard]
    },
    {
        path: 'usuario/biblioteca',
        component: BibliotecaComponent,
        canActivate: [authGuard]
    },
    {
        path: 'usuario/grupo',
        component: GrupoComponent,
        canActivate: [authGuard]
    },
    {
        path: 'usuario/:id',
        component: PerfilComponent,
        canActivate: [authGuard]
    },
];
