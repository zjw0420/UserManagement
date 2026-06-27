import client from './client';
import type { ApiResponse, AppointmentVo, IPage } from '../types';

export const appointmentApi = {
  page: (pageNum: number, pageSize: number, queryVo?: Record<string, unknown>) =>
    client.get<unknown, ApiResponse<IPage<AppointmentVo>>>('/admin/appointment/pageItem', { params: { pageNum, pageSize, ...queryVo } }),

  getDetailById: (id: number) =>
    client.get<unknown, ApiResponse<AppointmentVo>>('/admin/appointment/getDetailById', { params: { id } }),

  updateStatus: (id: number, status: number) =>
    client.put<unknown, ApiResponse>('/admin/appointment/updateStatus', null, { params: { id, status } }),

  delete: (id: number) =>
    client.delete<unknown, ApiResponse>(`/admin/appointment/${id}`),
};
