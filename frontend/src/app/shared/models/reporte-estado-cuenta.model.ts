export interface ReporteEstadoCuentaResponse {
  clienteId: string;
  cliente: ReporteClienteItem;
  from: string | null;
  to: string | null;
  cuentas: ReporteCuentaItem[];
  totalCreditos: number;
  totalDebitos: number;
  pdfBase64: string;
}

export interface ReporteClienteItem {
  id: string;
  identificacion: string;
  nombre: string;
  genero: string;
  edad: number;
  direccion: string;
  telefono: string;
  estado: boolean;
}

export interface ReporteCuentaItem {
  cuentaId: string;
  numeroCuenta: string;
  tipoCuenta: string;
  saldoInicial: number;
  saldoActual: number;
  movimientos: ReporteMovimientoItem[];
  totalCreditos: number;
  totalDebitos: number;
}

export interface ReporteMovimientoItem {
  movimientoId: string;
  cuentaId: string;
  numeroCuenta: string;
  tipoCuenta: string;
  fecha: string;
  tipoMovimiento: string;
  valor: number;
  saldoDisponible: number;
  estado: string;
  sagaId: string | null;
  commandId: string | null;
  createdAt: string;
}
