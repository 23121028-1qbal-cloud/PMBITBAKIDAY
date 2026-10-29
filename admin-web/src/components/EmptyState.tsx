import React from 'react';
import { Inbox } from 'lucide-react';

interface EmptyStateProps {
  title?: string;
  description?: string;
  action?: React.ReactNode;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  title = 'Tidak ada data ditemukan',
  description = 'Belum ada pendaftaran atau data yang sesuai dengan kriteria yang dipilih.',
  action,
}) => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '48px 24px',
        textAlign: 'center',
        background: '#FFFFFF',
        borderRadius: '8px',
        border: '1px dashed var(--color-border)',
        margin: '16px 0',
      }}
    >
      <div
        style={{
          width: '56px',
          height: '56px',
          borderRadius: '50%',
          background: '#F1F5F9',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'var(--color-text-secondary)',
          marginBottom: '16px',
        }}
      >
        <Inbox size={28} />
      </div>
      <h4
        style={{
          fontSize: '1rem',
          fontWeight: 600,
          color: 'var(--color-text-primary)',
          marginBottom: '6px',
        }}
      >
        {title}
      </h4>
      <p
        style={{
          fontSize: '0.875rem',
          color: 'var(--color-text-secondary)',
          maxWidth: '400px',
          marginBottom: action ? '20px' : '0',
        }}
      >
        {description}
      </p>
      {action && <div>{action}</div>}
    </div>
  );
};
