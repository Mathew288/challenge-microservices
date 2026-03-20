import { httpGet } from './http';

import { ReporteEstadoCuentaResponse } from '../../shared/models/reporte-estado-cuenta.model';

const base = '/reportes';

export interface EstadoCuentaQuery {
  clienteId: string;
  from?: string; // Instant ISO
  to?: string; // Instant ISO
}

export const reportesApi = {
  estadoCuenta: (query: EstadoCuentaQuery): Promise<ReporteEstadoCuentaResponse> => {
    const params = new URLSearchParams({ clienteId: query.clienteId });
    if (query.from) params.set('from', query.from);
    if (query.to) params.set('to', query.to);

    return httpGet<ReporteEstadoCuentaResponse>(`${base}/estado-cuenta?${params.toString()}`);
  },
};
