import { NavLink } from 'react-router-dom';
import { useTheme } from '../utils/theme';

const navItems = [
  { to: '/', label: '🏠 首页', end: true },
  { to: '/users', label: '👤 用户管理' },
  { to: '/apartments', label: '🏢 公寓管理' },
  { to: '/rooms', label: '🚪 房间管理' },
  { to: '/positions', label: '📋 岗位管理' },
  { to: '/rentals', label: '📝 租赁管理' },
  { to: '/dictionary', label: '📖 字典管理' },
  { to: '/appointments', label: '📅 预约管理' },
  { to: '/tenants', label: '👥 租客管理' },
];

export default function Sidebar() {
  const { theme, toggle } = useTheme();
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">🏘️ 物业管理系统</div>
      <nav className="sidebar-nav">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-footer">
        <span>v1.0</span>
        <button className="theme-toggle" onClick={toggle} title="切换主题">
          {theme === 'light' ? '🌙' : '☀️'}
        </button>
      </div>
    </aside>
  );
}
