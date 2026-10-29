import React from 'react';
import { ApplicationStatus } from '../types';

interface StatusBadgeProps {
  status: ApplicationStatus | string;
}

const statusMap: Record<string, { label: string; className: string }> = {
  draft: { label: 'Draft', className: 'badge-draft' },
  submitted: { label: 'Diajukan', className: 'badge-submitted' },
  review: { label: 'Dalam Pemeriksaan', className: 'badge-review' },
  revision: { label: 'Perlu Revisi', className: 'badge-revision' },
  resubmitted: { label: 'Diajukan Ulang', className: 'badge-submitted' },
  accepted: { label: 'Diterima', className: 'badge-accepted' },
  rejected: { label: 'Ditolak', className: 'badge-rejected' },
};

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status }) => {
  const config = statusMap[status] || {
    label: status,
    className: 'badge-draft',
  };

  return <span className={`badge ${config.className}`}>{config.label}</span>;
};
