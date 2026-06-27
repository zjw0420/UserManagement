import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi } from '../api/user';
import { apartmentApi } from '../api/apartment';
import { positionApi } from '../api/position';
import { rentalApi } from '../api/rental';

interface StatCard {
  title: string; count: number; icon: string; color: string; path: string;
}

export default function Dashboard() {
  const navigate = useNavigate();
  const [stats, setStats] = useState<StatCard[]>([
    { title: '用户总数', count: 0, icon: '👤', color: '#333', path: '/users' },
    { title: '公寓数量', count: 0, icon: '🏢', color: '#444', path: '/apartments' },
    { title: '岗位数量', count: 0, icon: '📋', color: '#555', path: '/positions' },
    { title: '租赁合同', count: 0, icon: '📝', color: '#666', path: '/rentals' },
  ]);
  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;

  useEffect(() => {
    (async () => {
      try {
        const [uRes, aRes, pRes, rRes] = await Promise.all([
          userApi.page(1, 1),
          apartmentApi.page(1, 1),
          positionApi.list(),
          rentalApi.page(1, 1),
        ]);
        setStats([
          { title: '用户总数', count: uRes.data?.total ?? 0, icon: '👤', color: '#333', path: '/users' },
          { title: '公寓数量', count: aRes.data?.total ?? 0, icon: '🏢', color: '#444', path: '/apartments' },
          { title: '岗位数量', count: pRes.data?.length ?? 0, icon: '📋', color: '#555', path: '/positions' },
          { title: '租赁合同', count: rRes.data?.total ?? 0, icon: '📝', color: '#666', path: '/rentals' },
        ]);
      } catch { /* */ }
    })();
  }, []);

  return (
    <div>
      <h1 className="page-title">欢迎回来{user ? `，${user.name || user.username}` : ''} 👋</h1>
      <p className="page-desc">物业管理后台 — 概览</p>
      <div className="stats-grid">
        {stats.map((s) => (
          <div key={s.title} className="stat-card" onClick={() => navigate(s.path)} style={{ borderTopColor: s.color }}>
            <div className="stat-icon" style={{ background: s.color }}>{s.icon}</div>
            <div className="stat-info"><div className="stat-num">{s.count}</div><div className="stat-label">{s.title}</div></div>
          </div>
        ))}
      </div>
      <div className="quick-links">
        <h3>快捷操作</h3>
        <div className="quick-grid">
          <button className="quick-btn" onClick={() => navigate('/users')}>➕ 新增用户</button>
          <button className="quick-btn" onClick={() => navigate('/apartments')}>➕ 新增公寓</button>
          <button className="quick-btn" onClick={() => navigate('/positions')}>➕ 新增岗位</button>
        </div>
      </div>
    </div>
  );
}
