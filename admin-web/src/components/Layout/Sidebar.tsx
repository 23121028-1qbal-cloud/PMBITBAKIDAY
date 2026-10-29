import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Users, FileCheck, Shield } from 'lucide-react';

export const Sidebar: React.FC = () => {

  const navItems = [
    {
      to: '/dashboard',
      label: 'Dashboard',
      icon: <LayoutDashboard size={18} />,
    },
    {
      to: '/applications',
      label: 'Daftar Pendaftar',
      icon: <Users size={18} />,
    },
  ];

  return (
    <aside className="sidebar">
      {/* Brand Header */}
      <div
        style={{
          height: '64px',
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          padding: '0 20px',
          borderBottom: '1px solid #1E293B',
        }}
      >
        <div
          style={{
            width: '36px',
            height: '36px',
            borderRadius: '8px',
            backgroundColor: 'var(--color-primary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#FFFFFF',
            fontWeight: 700,
          }}
        >
          AK
        </div>
        <div>
          <div style={{ fontWeight: 700, fontSize: '0.9375rem', letterSpacing: '0.025em' }}>
            ITB AKIDAY
          </div>
          <div style={{ fontSize: '0.6875rem', color: '#94A3B8' }}>
            Portal Admin PMB
          </div>
        </div>
      </div>

      {/* Nav List */}
      <nav style={{ padding: '16px 12px', flex: 1 }}>
        <div
          style={{
            fontSize: '0.6875rem',
            textTransform: 'uppercase',
            fontWeight: 600,
            color: '#64748B',
            padding: '0 8px 8px 8px',
            letterSpacing: '0.05em',
          }}
        >
          Menu Utama
        </div>
        <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '4px' }}>
          {navItems.map((item) => (
            <li key={item.to}>
              <NavLink
                to={item.to}
                style={({ isActive }) => ({
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '10px 12px',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                  fontWeight: 500,
                  color: isActive ? '#FFFFFF' : '#94A3B8',
                  backgroundColor: isActive ? 'var(--color-primary)' : 'transparent',
                  textDecoration: 'none',
                  transition: 'all 0.15s ease',
                })}
              >
                {item.icon}
                <span>{item.label}</span>
              </NavLink>
            </li>
          ))}
        </ul>

        <div
          style={{
            fontSize: '0.6875rem',
            textTransform: 'uppercase',
            fontWeight: 600,
            color: '#64748B',
            padding: '24px 8px 8px 8px',
            letterSpacing: '0.05em',
          }}
        >
          Informasi Sesi
        </div>
        <div
          style={{
            padding: '12px',
            background: '#1E293B',
            borderRadius: '6px',
            fontSize: '0.75rem',
            color: '#CBD5E1',
            display: 'flex',
            flexDirection: 'column',
            gap: '6px',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Shield size={14} style={{ color: 'var(--color-primary-light)' }} />
            <span>Sanctum Session Auth</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <FileCheck size={14} style={{ color: '#FCD34D' }} />
            <span>CSRF Protection Active</span>
          </div>
        </div>
      </nav>

      {/* Footer Info */}
      <div
        style={{
          padding: '16px 20px',
          borderTop: '1px solid #1E293B',
          fontSize: '0.75rem',
          color: '#64748B',
        }}
      >
        <div>PMB ITB Akiday v1.0</div>
        <div>Tahun Akademik 2026/2027</div>
      </div>
    </aside>
  );
};
