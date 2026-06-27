import client from './client';
import type { ApiResponse, ApartmentItemVo, ApartmentDetailVo, ApartmentSubmitVo, ApartmentQueryVo, IPage } from '../types';

export const apartmentApi = {
  page: (pageNum: number, pageSize: number, queryVo?: ApartmentQueryVo) =>
    client.get<unknown, ApiResponse<IPage<ApartmentItemVo>>>('/admin/apartment/pageItem', { params: { pageNum, pageSize, ...queryVo } }),

  getDetailById: (id: number) =>
    client.get<unknown, ApiResponse<ApartmentDetailVo>>('/admin/apartment/getDetailById', { params: { id } }),

  save: (data: ApartmentSubmitVo) =>
    client.post<unknown, ApiResponse>('/admin/apartment/saveOrUpdate', data),

  delete: (id: number) =>
    client.delete<unknown, ApiResponse>(`/admin/apartment/${id}`),
};
