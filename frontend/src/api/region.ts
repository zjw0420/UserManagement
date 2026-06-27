import client from './client';
import type { ApiResponse } from '../types';

export interface RegionVo { id: number; name: string; }

export const regionApi = {
  provinces: () => client.get<unknown, ApiResponse<RegionVo[]>>('/admin/region/province'),
  cities: (provinceId: number) => client.get<unknown, ApiResponse<RegionVo[]>>(`/admin/region/city/${provinceId}`),
  districts: (cityId: number) => client.get<unknown, ApiResponse<RegionVo[]>>(`/admin/region/district/${cityId}`),
};

export const fileApi = {
  upload: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return client.post<unknown, ApiResponse<string>>('/admin/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
