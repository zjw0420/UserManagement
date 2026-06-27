import axios from 'axios';

const client = axios.create({
  baseURL: '/',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

// 请求拦截：自动带 token
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截：统一提取 data + 检查业务 code
client.interceptors.response.use(
  (res) => {
    const body = res.data;
    // 业务码非 200 也视为错误
    if (body && typeof body === 'object' && 'code' in body && body.code !== 200) {
      if (body.code === 401 || body.code === 1005) {
        // Token 无效 → 跳登录
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
      return Promise.reject(new Error(body.message || '请求失败'));
    }
    return body;
  },
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
      return Promise.reject(err);
    }
    const msg = err.response?.data?.message || err.message || '网络错误';
    if (err.response?.status && err.response.status >= 400) {
      alert('请求失败 [' + err.response.status + ']: ' + msg);
    }
    return Promise.reject(new Error(msg));
  },
);

export default client;
