import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

export interface SelectOption<T> {
  value: T;
  label: string;
}

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './select.component.html',
  styleUrl: './select.component.css',
})
export class SelectComponent<T = string> {
  @Input() id?: string;
  @Input() label?: string;
  @Input() value: T | '' = '';
  @Input() options: SelectOption<T>[] = [];
  @Input() placeholder = 'Seleccione...';
  @Input() disabled = false;
  @Input() required = false;

  @Input() error?: string | null;
  @Input() hint?: string | null;

  @Output() valueChange = new EventEmitter<any>();
}
