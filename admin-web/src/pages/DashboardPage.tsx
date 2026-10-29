import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { fetchAdminApplications } from '../api/applications';
import { ApplicationSummary, PaginationMeta } from '../types';
import { extractApiError } from '../api/client';
import { StatusBadge } from '../components/StatusBadge';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Alert } from '../components/Alert';
import { EmptyState } from '../components/EmptyState';
import {
  Users,
  Clock,
  FileSearch,
  AlertCircle,
  CheckCircle2,
  XCircle,
  ArrowRight,
  RefreshCw,
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const [applications, setApplications] = useState<ApplicationSummary[]>([]);
  const [meta, setMeta] = useState<PaginationMeta | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const loadData = async () => {
    try {
      setIsLoading(true);
      setErrorMsg(null);
      const res = await fetchAdminApplications({ per_page: 50 });
      setApplications(res.data);
      if (res.meta) {
        setMeta(res.meta);
      }
    } catch (err) {
      const error = extractApiError(err);
      setErrorMsg(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Compute status distributions from actual returned application data
  const totalCount = meta ? meta.total : applications.length;
  const submittedCount = applications.filter((a) => a.status === 'submitted').length;
  const reviewCount = applications.filter((a) => a.status === 'review').length;
  const revisionCount = applications.filter((a) => a.status === 'revision').length;
  const acceptedCount = applications.filter((a) => a.status === 'accepted').length;
  const rejectedCount = applications.filter((a) => a.status === 'rejected').length;

  const statCards = [
    {
      title: 'Total Pendaftar',
      value: totalCount,
      icon: <Users size={24} style={{ color: 'var(--color-primary)' }} />,
      bgColor: 'var(--color-primary-light)',
      borderColor: 'var(--color-primary)',
    },
    {
      title: 'Menunggu Pemeriksaan',
      value: submittedCount,
      icon: <Clock size={24} style={{ color: 'var(--color-info)' }} />,
      bgColor: 'var(--color-info-bg)',
      borderColor: 'var(--color-info)',
    },
    {
      title: 'Dalam Pemeriksaan',
      value: reviewCount,
      icon: <FileSearch size={24} style={{ color: 'var(--color-warning)' }} />,
      bgColor: 'var(--color-warning-bg)',
      borderColor: 'var(--color-warning)',
    },
    {
      title: 'Perlu Revisi',
      value: revisionCount,
      icon: <AlertCircle size={24} style={{ color: 'var(--color-accent)' }} />,
      bgColor: 'var(--color-accent-light)',
      borderColor: 'var(--color-accent)',
    },
    {
      title: 'Diterima',
      value: acceptedCount,
      icon: <CheckCircle2 size={24} style={{ color: 'var(--color-success)' }} />,
      bgColor: 'var(--color-success-bg)',
      borderColor: 'var(--color-success)',
    },
    {
      title: 'Ditolak',
      value: rejectedCount,
      icon: <XCircle size={24} style={{ color: 'var(--color-danger)' }} />,
      bgColor: 'var(--color-danger-bg)',
      borderColor: 'var(--color-danger)',
    },
  ];

  return (
    <div>
      {/* Page Header */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '24px',
        }}
      >
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--color-text-primary)' }}>
            Dashboard Penerimaan Mahasiswa Baru
          </h1>
          <p style={{ fontSize: '0.875rem', color: 'var(--color-text-secondary)', marginTop: '4px' }}>
            Ringkasan status pendaftaran calon mahasiswa ITB Akiday
          </p>
        </div>
        <button
          onClick={loadData}
          className="btn btn-secondary"
          disabled={isLoading}
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <RefreshCw size={16} className={isLoading ? 'animate-spin' : ''} />
          <span>Muat Ulang</span>
        </button>
      </div>

      {errorMsg && (
        <Alert
          type="danger"
          message={errorMsg}
          onClose={() => setErrorMsg(null)}
        />
      )}

      {/* KPI Cards Grid */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '16px',
          marginBottom: '32px',
        }}
      >
        {statCards.map((card, idx) => (
          <div
            key={idx}
            className="card"
            style={{
              marginBottom: 0,
              padding: '20px',
              borderTop: `4px solid ${card.borderColor}`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
            }}
          >
            <div>
              <div style={{ fontSize: '0.8125rem', color: 'var(--color-text-secondary)', fontWeight: 500 }}>
                {card.title}
              </div>
              <div
                style={{
                  fontSize: '1.75rem',
                  fontWeight: 700,
                  color: 'var(--color-text-primary)',
                  marginTop: '4px',
                }}
              >
                {isLoading ? '-' : card.value}
              </div>
            </div>
            <div
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '10px',
                backgroundColor: card.bgColor,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              {card.icon}
            </div>
          </div>
        ))}
      </div>

      {/* Recent Applications Section */}
      <div className="card">
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            marginBottom: '16px',
          }}
        >
          <h2 className="card-title" style={{ margin: 0 }}>
            Pendaftaran Terbaru
          </h2>
          <Link
            to="/applications"
            style={{
              fontSize: '0.875rem',
              fontWeight: 500,
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
            }}
          >
            <span>Lihat Semua Pendaftar</span>
            <ArrowRight size={16} />
          </Link>
        </div>

        {isLoading ? (
          <LoadingSpinner message="Mengambil daftar pendaftaran terbaru..." />
        ) : applications.length === 0 ? (
          <EmptyState
            title="Belum Ada Pendaftaran Masuk"
            description="Belum ada calon mahasiswa yang mengajukan berkas pendaftaran ke sistem."
          />
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>No. Pendaftaran</th>
                  <th>Nama Calon Mahasiswa</th>
                  <th>Program Studi</th>
                  <th>Gelombang</th>
                  <th>Status</th>
                  <th>Waktu Pengajuan</th>
                  <th style={{ textAlign: 'right' }}>Aksi</th>
                </tr>
              </thead>
              <tbody>
                {applications.slice(0, 10).map((app) => (
                  <tr key={app.id}>
                    <td style={{ fontWeight: 600 }}>{app.registration_number}</td>
                    <td>
                      <div style={{ fontWeight: 500 }}>{app.applicant?.full_name || '-'}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--color-text-secondary)' }}>
                        NIK: {app.applicant?.nik || '-'}
                      </div>
                    </td>
                    <td>{app.study_program?.name || '-'}</td>
                    <td>{app.wave?.name || '-'}</td>
                    <td>
                      <StatusBadge status={app.status} />
                    </td>
                    <td style={{ fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>
                      {app.submitted_at
                        ? new Date(app.submitted_at).toLocaleDateString('id-ID', {
                            day: 'numeric',
                            month: 'short',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                          })
                        : '-'}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <Link
                        to={`/applications/${app.id}`}
                        className="btn btn-secondary btn-sm"
                      >
                        Detail
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
