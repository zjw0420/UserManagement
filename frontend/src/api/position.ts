import client from './client';
import type { ApiResponse, SystemPost } from '../types';

export const positionApi = {
  list: () =>
    client.get<unknown, ApiResponse<SystemPost[]>>('/admin/post/list'),

  getById: (id: number) =>
    client.get<unknown, ApiResponse<SystemPost>>(`/admin/post/${id}`),

  save: (data: SystemPost) =>
    client.post<unknown, ApiResponse>('/admin/post/save', data),

  delete: (id: number) =>
    client.delete<unknown, ApiResponse>(`/admin/post/${id}`),
};
