import { httpDelete, httpGet, httpPatch, httpPost } from './http';

import { Cliente, ClienteCreateRequest, ClienteUpdateRequest } from '../../shared/models/cliente.model';

const base = '/clientes';

export const clientesApi = {
  list: (): Promise<Cliente[]> => httpGet<Cliente[]>(base),
  getById: (id: string): Promise<Cliente> => httpGet<Cliente>(`${base}/${id}`),
  create: (body: ClienteCreateRequest): Promise<Cliente> => httpPost<Cliente, ClienteCreateRequest>(base, body),
  update: (id: string, body: ClienteUpdateRequest): Promise<Cliente> =>
    httpPatch<Cliente, ClienteUpdateRequest>(`${base}/${id}`, body),
  remove: (id: string): Promise<void> => httpDelete<void>(`${base}/${id}`),
};
