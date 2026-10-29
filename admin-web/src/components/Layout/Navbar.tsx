import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { LogOut, User as UserIcon } from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();

  const handleLogout = async () => {
    if (window.confirm('Apakah Anda yakin ingin keluar dari sistem admin?')) {
      await logout();
      window.location.href = '/login';
    }
  };

  return (
    <header className="topbar">
      <div>
        <h2 style={{ fontSize: '1rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>
          Panel Manajemen Penerimaan Mahasiswa Baru
        </h2>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <div
            style={{
              width: '32px',
              height: '32px',
              borderRadius: '50%',
              backgroundColor: '#E2E8F0',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--color-primary)',
            }}
          >
            <UserIcon size={18} />
          </div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <span style={{ fontSize: '0.8125rem', fontWeight: 600 }}>{user?.email}</span>
            <span style={{ fontSize: '0.6875rem', color: 'var(--color-text-secondary)', textTransform: 'capitalize' }}>
              Peran: {user?.role}
            </span>
          </div>
        </div>
        <button
          onClick={handleLogout}
          className="btn btn-secondary btn-sm"
          title="Keluar dari akun"
          style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
        >
          <LogOut size={16} />
          <span>Keluar</span>
        </button>
      </div>
    </header>
  );
};
