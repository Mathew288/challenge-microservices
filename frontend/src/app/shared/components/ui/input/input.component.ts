import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css',
})
export class InputComponent {
  @Input() id?: string;
  @Input() label?: string;
  @Input() placeholder?: string;
  @Input() type: 'text' | 'number' | 'password' | 'tel' | 'datetime-local' | 'date' = 'text';
  @Input() value: string | number | null = '';
  @Input() disabled = false;
  @Input() required = false;
  @Input() error?: string | null;
  @Input() hint?: string | null;


  @Output() valueChange = new EventEmitter<string>();

  onInput(ev: Event) {
    const next = (ev.target as HTMLInputElement).value;
    this.valueChange.emit(next);
  }
}
