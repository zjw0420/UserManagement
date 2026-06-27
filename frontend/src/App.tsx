import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from './utils/theme';
import Login from './pages/Login';
import Layout from './pages/Layout';
import Dashboard from './pages/Dashboard';
import UserList from './pages/UserList';
import ApartmentList from './pages/ApartmentList';
import PositionList from './pages/PositionList';
import RentalList from './pages/RentalList';
import RoomList from './pages/RoomList';
import Dictionary from './pages/Dictionary';
import AppointmentList from './pages/AppointmentList';
import TenantList from './pages/TenantList';

export default function App() {
  return (
    <ThemeProvider>
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/users" element={<UserList />} />
          <Route path="/apartments" element={<ApartmentList />} />
          <Route path="/positions" element={<PositionList />} />
          <Route path="/rentals" element={<RentalList />} />
          <Route path="/rooms" element={<RoomList />} />
          <Route path="/dictionary" element={<Dictionary />} />
          <Route path="/appointments" element={<AppointmentList />} />
          <Route path="/tenants" element={<TenantList />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
    </ThemeProvider>
  );
}
