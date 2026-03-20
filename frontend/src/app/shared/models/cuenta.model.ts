import { TipoCuenta } from './enums';

export interface Cuenta {
  id: string; // UUID
  numeroCuenta: string;
  tipo: TipoCuenta;
  saldoInicial: number;
  saldoActual: number;
  estado: boolean;
  clienteId: string; // UUID
  createdAt?: string; // Instant
  updatedAt?: string; // Instant
}

export interface CuentaCreateRequest {
  numeroCuenta: string;
  tipo: TipoCuenta;
  saldoInicial: number;
  estado: boolean;
  clienteId: string; // UUID
}

export interface CuentaUpdateRequest {
  numeroCuenta?: string;
  tipo?: TipoCuenta;
  saldoInicial?: number;
  estado: boolean; // backend lo pide requerido en UpdateCuentaRequest
}
