import client from './client'; // axios instance
import type { ApiResponse, IPage } from '../types';

export interface ClientVo {
  id: number; phone: string; nickname: string;
  avatarUrl?: string; status: number;
  createTime: string;
}

export const clientApi = {
  page: (pageNum: number, pageSize: number, keyword?: string) =>
    client.get<unknown, ApiResponse<IPage<ClientVo>>>('/admin/client/page', { params: { pageNum, pageSize, keyword } }),

  getById: (id: number) =>
    client.get<unknown, ApiResponse<ClientVo>>(`/admin/client/${id}`),
};
