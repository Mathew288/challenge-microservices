import { AxiosRequestConfig } from 'axios';

import { ApiResponse } from './api-response.model';
import { api } from './axios.instance';

export async function httpGet<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const res = await api.get<ApiResponse<T>>(url, config);
  return res.data.data;
}

export async function httpPost<T, B>(
  url: string,
  body: B,
  config?: AxiosRequestConfig
): Promise<T> {
  const res = await api.post<ApiResponse<T>>(url, body, config);
  return res.data.data;
}

export async function httpPut<T, B>(
  url: string,
  body: B,
  config?: AxiosRequestConfig
): Promise<T> {
  const res = await api.put<ApiResponse<T>>(url, body, config);
  return res.data.data;
}

export async function httpPatch<T, B>(
  url: string,
  body: B,
  config?: AxiosRequestConfig
): Promise<T> {
  const res = await api.patch<ApiResponse<T>>(url, body, config);
  return res.data.data;
}

export async function httpDelete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const res = await api.delete<ApiResponse<T>>(url, config);
  return res.data.data;
}
