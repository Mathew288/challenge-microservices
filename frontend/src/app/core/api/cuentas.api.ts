import { httpDelete, httpGet, httpPatch, httpPost } from './http';

import { Cuenta, CuentaCreateRequest, CuentaUpdateRequest } from '../../shared/models/cuenta.model';

const base = '/cuentas';

export const cuentasApi = {
  list: (): Promise<Cuenta[]> => httpGet<Cuenta[]>(base),
  getById: (id: string): Promise<Cuenta> => httpGet<Cuenta>(`${base}/${id}`),
  create: (body: CuentaCreateRequest): Promise<Cuenta> => httpPost<Cuenta, CuentaCreateRequest>(base, body),
  update: (id: string, body: CuentaUpdateRequest): Promise<Cuenta> =>
    httpPatch<Cuenta, CuentaUpdateRequest>(`${base}/${id}`, body),
  remove: (id: string): Promise<void> => httpDelete<void>(`${base}/${id}`),
};
