import React from 'react';
import { Link } from 'react-router-dom';
import { AlertCircle } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '60vh',
        textAlign: 'center',
        padding: '32px',
      }}
    >
      <AlertCircle size={48} style={{ color: 'var(--color-danger)', marginBottom: '16px' }} />
      <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>404 — Halaman Tidak Ditemukan</h1>
      <p style={{ color: 'var(--color-text-secondary)', marginTop: '8px', maxWidth: '400px' }}>
        Halaman atau rute yang Anda tuju tidak tersedia pada portal administrasi PMB ITB Akiday.
      </p>
      <Link to="/dashboard" className="btn btn-primary" style={{ marginTop: '24px' }}>
        Kembali ke Dashboard
      </Link>
    </div>
  );
};
