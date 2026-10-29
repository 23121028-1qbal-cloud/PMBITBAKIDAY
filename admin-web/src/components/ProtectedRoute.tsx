import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LoadingSpinner } from './LoadingSpinner';
import { UserRole } from '../types';

interface ProtectedRouteProps {
  allowedRoles?: UserRole[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  allowedRoles = ['admin', 'panitia', 'pimpinan'],
}) => {
  const { user, isLoading, hasRole } = useAuth();

  if (isLoading) {
    return (
      <div
        style={{
          display: 'flex',
          height: '100vh',
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: 'var(--color-background)',
        }}
      >
        <LoadingSpinner message="Memeriksa sesi login admin..." />
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0 && !hasRole(allowedRoles)) {
    return (
      <div style={{ padding: '48px', textAlign: 'center' }}>
        <h2>Akses Ditolak (403 Forbidden)</h2>
        <p style={{ marginTop: '12px', color: 'var(--color-text-secondary)' }}>
          Akun Anda ({user.email}) memiliki peran <strong>{user.role}</strong> yang tidak memiliki izin untuk mengakses area ini.
        </p>
      </div>
    );
  }

  return <Outlet />;
};
