import client from './client';
import type { ApiResponse, RoomItemVo, RoomDetailVo, RoomSubmitVo, IPage } from '../types';

export const roomApi = {
  page: (pageNum: number, pageSize: number, queryVo?: Record<string, unknown>) =>
    client.get<unknown, ApiResponse<IPage<RoomItemVo>>>('/admin/room/pageItem', { params: { pageNum, pageSize, ...queryVo } }),

  getDetailById: (id: number) =>
    client.get<unknown, ApiResponse<RoomDetailVo>>('/admin/room/getDetailById', { params: { id } }),

  save: (data: RoomSubmitVo) =>
    client.post<unknown, ApiResponse>('/admin/room/saveOrUpdate', data),

  delete: (id: number) =>
    client.delete<unknown, ApiResponse>(`/admin/room/${id}`),
};
