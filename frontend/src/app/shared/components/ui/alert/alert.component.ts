import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

type AlertType = 'info' | 'success' | 'warning' | 'error';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.css',
})
export class AlertComponent {
  @Input() type: AlertType = 'info';
  @Input() message = '';
}
