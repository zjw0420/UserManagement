import client from './client';
import type { ApiResponse, AgreementVo, AgreementQueryVo, AgreementSubmitVo, IPage } from '../types';

export const rentalApi = {
  page: (pageNum: number, pageSize: number, queryVo?: AgreementQueryVo) =>
    client.get<unknown, ApiResponse<IPage<AgreementVo>>>('/admin/agreement/pageItem', { params: { pageNum, pageSize, ...queryVo } }),

  getDetailById: (id: number) =>
    client.get<unknown, ApiResponse<AgreementVo>>('/admin/agreement/getDetailById', { params: { id } }),

  save: (data: AgreementSubmitVo) =>
    client.post<unknown, ApiResponse>('/admin/agreement/saveOrUpdate', data),

  delete: (id: number) =>
    client.delete<unknown, ApiResponse>(`/admin/agreement/${id}`),
};
