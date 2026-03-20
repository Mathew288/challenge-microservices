import { httpDelete, httpGet, httpPatch, httpPost } from './http';

import {
  Movimiento,
  MovimientoCreateRequest,
  MovimientoUpdateRequest,
} from '../../shared/models/movimiento.model';

const base = '/movimientos';

export const movimientosApi = {
  list: (): Promise<Movimiento[]> => httpGet<Movimiento[]>(base),
  getById: (id: string): Promise<Movimiento> => httpGet<Movimiento>(`${base}/${id}`),
  create: (body: MovimientoCreateRequest): Promise<Movimiento> =>
    httpPost<Movimiento, MovimientoCreateRequest>(base, body),
  update: (id: string, body: MovimientoUpdateRequest): Promise<Movimiento> =>
    httpPatch<Movimiento, MovimientoUpdateRequest>(`${base}/${id}`, body),
  remove: (id: string): Promise<void> => httpDelete<void>(`${base}/${id}`),
};
