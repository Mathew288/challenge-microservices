import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

type SidebarNavItem = Readonly<{
  label: string;
  path: string;
}>;

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  readonly brand = 'Banking Challenge';

  readonly navItems: readonly SidebarNavItem[] = [
    { label: 'Clientes', path: '/clientes' },
    { label: 'Cuentas', path: '/cuentas' },
    { label: 'Movimientos', path: '/movimientos' },
    { label: 'Reportes', path: '/reportes' },
  ];
}
