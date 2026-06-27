import client from './client';
import type { ApiResponse } from '../types';

export interface KVItem {
  id?: number; name: string;
  attrKeyId?: number; feeKeyId?: number;
  unit?: string;
}

export const attrApi = {
  keyList: () => client.get<unknown, ApiResponse<KVItem[]>>('/admin/attr/key/list'),
  keySave: (data: KVItem) => client.post<unknown, ApiResponse>('/admin/attr/key/save', data),
  keyDelete: (id: number) => client.delete<unknown, ApiResponse>(`/admin/attr/key/${id}`),
  valueList: (keyId: number) => client.get<unknown, ApiResponse<KVItem[]>>('/admin/attr/value/list', { params: { keyId } }),
  valueSave: (data: KVItem) => client.post<unknown, ApiResponse>('/admin/attr/value/save', data),
  valueDelete: (id: number) => client.delete<unknown, ApiResponse>(`/admin/attr/value/${id}`),
};

export const feeApi = {
  keyList: () => client.get<unknown, ApiResponse<KVItem[]>>('/admin/fee/key/list'),
  keySave: (data: KVItem) => client.post<unknown, ApiResponse>('/admin/fee/key/save', data),
  keyDelete: (id: number) => client.delete<unknown, ApiResponse>(`/admin/fee/key/${id}`),
  valueList: (keyId: number) => client.get<unknown, ApiResponse<KVItem[]>>('/admin/fee/value/list', { params: { keyId } }),
  valueSave: (data: KVItem) => client.post<unknown, ApiResponse>('/admin/fee/value/save', data),
  valueDelete: (id: number) => client.delete<unknown, ApiResponse>(`/admin/fee/value/${id}`),
};
