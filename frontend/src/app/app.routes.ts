import { Routes } from '@angular/router';

import { MainLayoutComponent } from './shared/components/layout/main-layout/main-layout.component';
import { HomePage } from './features/home/home.page';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', component: HomePage },
      { path: 'clientes', loadChildren: () => import('./features/clientes/clientes.routes') },
      { path: 'cuentas', loadChildren: () => import('./features/cuentas/cuentas.routes') },
      { path: 'movimientos', loadChildren: () => import('./features/movimientos/movimientos.routes') },
      { path: 'reportes', loadChildren: () => import('./features/reportes/reportes.routes') },
    ],
  },
  { path: '**', redirectTo: '' },
];
