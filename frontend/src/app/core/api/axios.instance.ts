import axios, { AxiosError } from 'axios';

import { environment } from '../config/environment';

export const api = axios.create({
  baseURL: environment.apiBaseUrl,
  timeout: 15000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  // Correlation id simple para trazabilidad (también lo genera el backend si no viene)
  const correlationId =
    crypto?.randomUUID?.() ?? `${Date.now()}-${Math.random().toString(16).slice(2)}`;

  config.headers = config.headers ?? {};
  config.headers['X-Correlation-Id'] = correlationId;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<any>) => {
    // Normalizamos a Error con mensaje legible para UI
    const message =
      error.response?.data?.message ||
      error.response?.data?.error ||
      error.message ||
      'Error inesperado';

    return Promise.reject(new Error(message));
  }
);
