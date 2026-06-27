import client from './client';
import type { ApiResponse, CaptchaVo, LoginForm, SystemUserItemVo, SystemUserInfoVo, SystemUserQueryVo, IPage } from '../types';

export const authApi = {
  captcha: () => client.get<unknown, ApiResponse<CaptchaVo>>('/admin/captcha'),
  login: (data: LoginForm) => client.post<unknown, ApiResponse<string>>('/admin/login', data),
};

export const userApi = {
  page: (pageNum: number, pageSize: number, queryVo?: SystemUserQueryVo) =>
    client.get<unknown, ApiResponse<IPage<SystemUserItemVo>>>('/admin/user/page', { params: { pageNum, pageSize, ...queryVo } }),

  getById: (id: number) =>
    client.get<unknown, ApiResponse<SystemUserInfoVo>>(`/admin/user/${id}`),

  create: (data: SystemUserInfoVo) =>
    client.post<unknown, ApiResponse>('/admin/user/save', data),

  update: (data: SystemUserInfoVo) =>
    client.put<unknown, ApiResponse>('/admin/user/update', data),

  delete: (id: number) =>
    client.delete<unknown, ApiResponse>(`/admin/user/${id}`),

  kick: (id: number) =>
    client.post<unknown, ApiResponse>(`/admin/user/kick/${id}`),
};
