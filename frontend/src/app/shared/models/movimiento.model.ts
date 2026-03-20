import { EstadoMovimiento, TipoMovimiento } from './enums';

export interface Movimiento {
  id: string; // UUID
  cuentaId: string; // UUID
  fecha: string; // Instant
  tipo: TipoMovimiento;
  valor: number;
  saldoDisponible: number;
  estado: EstadoMovimiento;
  sagaId?: string;
  commandId?: string;
  createdAt?: string; // Instant
}

export interface MovimientoCreateRequest {
  cuentaId: string;
  tipo: TipoMovimiento;
  valor: number;
}

export interface MovimientoUpdateRequest {
  tipo?: TipoMovimiento;
  valor?: number;
}
