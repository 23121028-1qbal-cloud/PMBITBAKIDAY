import React from 'react';
import { Loader2 } from 'lucide-react';

export const LoadingSpinner: React.FC<{ message?: string }> = ({
  message = 'Memuat data...',
}) => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '48px 16px',
        color: 'var(--color-text-secondary)',
        gap: '12px',
      }}
    >
      <Loader2
        size={32}
        style={{
          animation: 'spin 1s linear infinite',
          color: 'var(--color-primary)',
        }}
      />
      <span style={{ fontSize: '0.875rem' }}>{message}</span>
      <style>{`
        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};
