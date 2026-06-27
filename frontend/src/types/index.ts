// ====== 通用 ======
export interface ApiResponse<T = unknown> {
  code: number;
  message?: string;
  data?: T;
}

export interface IPage<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// ====== 登录 ======
export interface CaptchaVo { key: string; imageBase64: string }
export interface LoginForm { username: string; password: string; captchaKey: string; captchaCode: string }

// ====== 系统用户 (system_user) ======
export interface SystemUserItemVo {
  id: number; username: string; name: string;
  type: number;       // 0:GENERAL 1:ADMIN
  phone: string; postName?: string;
  status: number;     // 1:正常 0:禁用
  createTime: string;
}

export interface SystemUserInfoVo {
  id?: number; username: string; password?: string;
  name: string; type: number; phone: string;
  avatarUrl?: string; postId?: number;
  status: number; additionalInfo?: string;
  createTime?: string; updateTime?: string;
}

export interface SystemUserQueryVo {
  keyword?: string; type?: number; status?: number;
}

// ====== 岗位 (system_post) ======
export interface SystemPost {
  id?: number; postCode: string; name: string;
  description: string;
  status: string;     // "1":启用 "0":禁用
  createTime?: string; updateTime?: string;
}

// ====== 公寓 (apartment_info) ======
export interface ApartmentItemVo {
  id: number; name: string; introduction: string;
  provinceName?: string; cityName?: string; districtName?: string;
  addressDetail: string; phone?: string;
  minRent?: number; roomCount?: number;
  isRelease: number;  // 1:已发布 0:未发布
}

export interface ApartmentDetailVo extends ApartmentItemVo {
  provinceId?: number; cityId?: number; districtId?: number;
  latitude?: string; longitude?: string;
  facilityInfoList?: { id: number; name: string }[];
  labelInfoList?: { id: number; name: string }[];
  feeValueList?: { id: number; name: string; price?: number }[];
  graphVoList?: GraphVo[];
}

export interface ApartmentSubmitVo {
  id?: number; name: string; introduction: string;
  provinceId?: number; provinceName?: string;
  cityId?: number; cityName?: string;
  districtId?: number; districtName?: string;
  addressDetail: string; latitude?: string; longitude?: string;
  phone?: string; isRelease: number;
  facilityInfoIds?: number[]; labelIds?: number[];
  feeValueIds?: number[]; graphVoList?: GraphVo[];
}

export interface ApartmentQueryVo {
  provinceId?: number; cityId?: number; districtId?: number;
  name?: string; isRelease?: number;
}

// ====== 房间 (room_info) ======
export interface RoomItemVo {
  id: number; roomNumber: string; rent: number;
  apartmentId: number; apartmentName?: string;
  isRelease: number;
}

export interface RoomDetailVo extends RoomItemVo {
  facilityInfoList?: { id: number; name: string }[];
  labelInfoList?: { id: number; name: string }[];
  attrValueList?: { id: number; name: string }[];
  leaseTermList?: { id: number; name: string }[];
  paymentTypeList?: { id: number; name: string }[];
}

export interface RoomSubmitVo {
  id?: number; roomNumber: string; rent: number;
  apartmentId: number; isRelease: number;
  facilityInfoIds?: number[]; labelIds?: number[];
  attrValueIds?: number[]; leaseTermIds?: number[];
  paymentTypeIds?: number[];
}

// ====== 租赁合同 (lease_agreement) ======
export interface AgreementVo {
  id: number; name: string; phone: string;
  identificationNumber?: string;
  apartmentId: number; roomId?: number; roomNumber?: string;
  leaseStartDate: string; leaseEndDate: string;
  rent: number; deposit: number;
  status: number; sourceType?: number; additionalInfo?: string;
}

export interface AgreementSubmitVo {
  id?: number; name: string; phone: string;
  identificationNumber?: string;
  apartmentId: number; roomId?: number;
  leaseStartDate: string; leaseEndDate: string;
  rent: number; deposit: number;
  status: number; sourceType?: number; additionalInfo?: string;
}

export interface AgreementQueryVo {
  apartmentId?: number; roomId?: number;
  name?: string; phone?: string; status?: number;
}

// ====== 看房预约 (view_appointment) ======
export interface AppointmentVo {
  id: number; userId?: number;
  name: string; phone: string;
  apartmentId: number; appointmentTime: string;
  appointmentStatus: number;  // 1:待看房 2:已取消 3:已看房
  additionalInfo?: string;
}

// ====== 图片 ======
export interface GraphVo { name: string; url: string }
