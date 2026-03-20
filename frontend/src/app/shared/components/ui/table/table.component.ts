import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

export interface TableColumn<T> {
  key?: keyof T | string;
  header: string;
  width?: string;
  cell?: (row: T) => string;
}

export type TableRowKey<T> = keyof T | ((row: T) => string);

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './table.component.html',
  styleUrl: './table.component.css',
})
export class TableComponent<T extends object> {
  @Input({ required: true }) columns: TableColumn<T>[] = [];
  @Input() rows: T[] = [];
  @Input() rowKey?: TableRowKey<T>;
  @Input() emptyText = 'Sin resultados';

  trackByRow = (index: number, row: T) => {
    if (!this.rowKey) return index;

    if (typeof this.rowKey === 'function') return this.rowKey(row);

    const key = this.rowKey as keyof T;
    return String((row as any)[key] ?? index);
  };

  cellText(col: TableColumn<T>, row: T) {
    if (col.cell) return col.cell(row);
    if (!col.key) return '';
    return String((row as any)[col.key as any] ?? '');
  }
}
