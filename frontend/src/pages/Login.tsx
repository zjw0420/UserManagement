import { useState, useEffect, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi, userApi } from '../api/user';

/** 解析 JWT payload (不验签) */
function parseJwt(token: string): { sub?: string; version?: number } | null {
  try {
    const payload = token.split('.')[1];
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(json);
  } catch {
    return null;
  }
}

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [captchaCode, setCaptchaCode] = useState('');
  const [captchaKey, setCaptchaKey] = useState('');
  const [captchaImage, setCaptchaImage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const fetchCaptcha = async () => {
    try {
      const res = await authApi.captcha();
      if (res.data) {
        setCaptchaKey(res.data.key);
        setCaptchaImage(res.data.imageBase64);
      }
    } catch {
      setError('获取验证码失败');
    }
  };

  useEffect(() => {
    fetchCaptcha();
  }, []);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setError('请输入用户名和密码');
      return;
    }
    if (!captchaCode.trim()) {
      setError('请输入验证码');
      return;
    }
    setLoading(true);
    setError('');
    try {
      // 1. 登录获取 token
      const loginRes = await authApi.login({
        username,
        password,
        captchaKey,
        captchaCode,
      });
      if (loginRes.code !== 200 || !loginRes.data) {
        setError(loginRes.message || '登录失败');
        fetchCaptcha();
        setLoading(false);
        return;
      }
      const token = loginRes.data!;
      localStorage.setItem('token', token);

      // 2. 从 JWT 解析 userId，获取用户信息
      const jwt = parseJwt(token);
      if (jwt?.sub) {
        try {
          const userRes = await userApi.getById(Number(jwt.sub));
          if (userRes.data) {
            localStorage.setItem('user', JSON.stringify(userRes.data));
          }
        } catch { /* 获取用户信息失败不影响登录 */ }
      }

      navigate('/', { replace: true });
    } catch (err: unknown) {
      fetchCaptcha();
      setError(err instanceof Error ? err.message : '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleSubmit}>
        <h1>🏘️</h1>
        <h2>物业管理系统</h2>
        <p className="login-subtitle">请输入账号密码登录</p>
        {error && <div className="form-error">{error}</div>}
        <div className="form-group">
          <label>用户名</label>
          <input
            type="text"
            placeholder="admin"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
          />
        </div>
        <div className="form-group">
          <label>密码</label>
          <input
            type="password"
            placeholder="123456"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label>验证码</label>
          <div className="captcha-row">
            <input
              type="text"
              placeholder="请输入验证码"
              value={captchaCode}
              onChange={(e) => setCaptchaCode(e.target.value)}
              className="captcha-input"
            />
            {captchaImage ? (
              <img
                src={captchaImage}
                alt="验证码"
                className="captcha-img"
                onClick={fetchCaptcha}
                title="点击刷新"
              />
            ) : (
              <span className="captcha-loading">加载中...</span>
            )}
          </div>
        </div>
        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? '登录中...' : '登 录'}
        </button>
      </form>
    </div>
  );
}
