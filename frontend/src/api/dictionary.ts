import client from './client';
import type { ApiResponse } from '../types';

export interface DictEntity {
  id?: number; name: string;
  [key: string]: unknown;
}

const dictApi = (prefix: string) => ({
  list: () => client.get<unknown, ApiResponse<DictEntity[]>>(`/admin/${prefix}/list`),
  getById: (id: number) => client.get<unknown, ApiResponse<DictEntity>>(`/admin/${prefix}/${id}`),
  save: (data: DictEntity) => client.post<unknown, ApiResponse>(`/admin/${prefix}/save`, data),
  delete: (id: number) => client.delete<unknown, ApiResponse>(`/admin/${prefix}/${id}`),
});

export const facilityApi = dictApi('facility');
export const labelApi = dictApi('label');
export const leaseTermApi = dictApi('lease-term');
export const paymentTypeApi = dictApi('payment');
