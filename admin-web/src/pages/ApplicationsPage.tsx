import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { fetchAdminApplications } from '../api/applications';
import { fetchAdmissionWaves, fetchStudyPrograms } from '../api/references';
import {
  AdmissionWave,
  ApplicationSummary,
  PaginationMeta,
  StudyProgram,
} from '../types';
import { extractApiError } from '../api/client';
import { StatusBadge } from '../components/StatusBadge';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Alert } from '../components/Alert';
import { EmptyState } from '../components/EmptyState';
import {
  Search,
  Filter,
  X,
  ChevronLeft,
  ChevronRight,
  RefreshCw,
} from 'lucide-react';

export const ApplicationsPage: React.FC = () => {
  const [applications, setApplications] = useState<ApplicationSummary[]>([]);
  const [waves, setWaves] = useState<AdmissionWave[]>([]);
  const [studyPrograms, setStudyPrograms] = useState<StudyProgram[]>([]);
  const [meta, setMeta] = useState<PaginationMeta | null>(null);

  // Filter states
  const [page, setPage] = useState<number>(1);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [waveFilter, setWaveFilter] = useState<string>('');
  const [programFilter, setProgramFilter] = useState<string>('');
  const [searchQuery, setSearchQuery] = useState<string>('');

  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // Load reference data once
  useEffect(() => {
    const loadReferences = async () => {
      try {
        const [wavesData, progsData] = await Promise.all([
          fetchAdmissionWaves(),
          fetchStudyPrograms(),
        ]);
        setWaves(wavesData);
        setStudyPrograms(progsData);
      } catch (err) {
        console.warn('Gagal memuat data referensi filter:', err);
      }
    };
    loadReferences();
  }, []);

  // Fetch applications list
  const loadApplications = useCallback(async () => {
    try {
      setIsLoading(true);
      setErrorMsg(null);

      const params: Record<string, string | number> = {
        page,
        per_page: 15,
      };

      if (statusFilter) params.status = statusFilter;
      if (waveFilter) params.wave_id = waveFilter;
      if (programFilter) params.study_program_id = programFilter;

      const res = await fetchAdminApplications(params);
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
  }, [page, statusFilter, waveFilter, programFilter]);

  useEffect(() => {
    loadApplications();
  }, [loadApplications]);

  const handleResetFilters = () => {
    setStatusFilter('');
    setWaveFilter('');
    setProgramFilter('');
    setSearchQuery('');
    setPage(1);
  };

  const isFilterActive =
    Boolean(statusFilter) ||
    Boolean(waveFilter) ||
    Boolean(programFilter) ||
    Boolean(searchQuery);

  // Client-side search on current page items by registration number or full name
  const filteredApplications = applications.filter((app) => {
    if (!searchQuery.trim()) return true;
    const query = searchQuery.toLowerCase();
    const regNo = app.registration_number.toLowerCase();
    const name = (app.applicant?.full_name || '').toLowerCase();
    const nik = (app.applicant?.nik || '').toLowerCase();
    return regNo.includes(query) || name.includes(query) || nik.includes(query);
  });

  return (
    <div>
      {/* Header */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '20px',
        }}
      >
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--color-text-primary)' }}>
            Daftar Pengajuan Mahasiswa Baru
          </h1>
          <p style={{ fontSize: '0.875rem', color: 'var(--color-text-secondary)', marginTop: '4px' }}>
            Pantau, periksa berkas, dan tetapkan keputusan seleksi calon mahasiswa
          </p>
        </div>
        <button
          onClick={loadApplications}
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

      {/* Filter and Search Bar */}
      <div className="card" style={{ padding: '16px 20px', marginBottom: '20px' }}>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
            gap: '12px',
            alignItems: 'flex-end',
          }}
        >
          {/* Search Input */}
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" style={{ fontSize: '0.8125rem' }}>
              Cari Pendaftar
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="form-input"
                style={{ paddingLeft: '36px' }}
                placeholder="No. Reg / Nama / NIK..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              <Search
                size={16}
                style={{
                  position: 'absolute',
                  left: '12px',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: '#94A3B8',
                }}
              />
            </div>
          </div>

          {/* Status Filter */}
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" style={{ fontSize: '0.8125rem' }}>
              Status Pendaftaran
            </label>
            <select
              className="form-select"
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                setPage(1);
              }}
            >
              <option value="">Semua Status</option>
              <option value="submitted">Diajukan (Submitted)</option>
              <option value="review">Dalam Pemeriksaan (Review)</option>
              <option value="revision">Perlu Revisi (Revision)</option>
              <option value="accepted">Diterima (Accepted)</option>
              <option value="rejected">Ditolak (Rejected)</option>
            </select>
          </div>

          {/* Wave Filter */}
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" style={{ fontSize: '0.8125rem' }}>
              Gelombang Penerimaan
            </label>
            <select
              className="form-select"
              value={waveFilter}
              onChange={(e) => {
                setWaveFilter(e.target.value);
                setPage(1);
              }}
            >
              <option value="">Semua Gelombang</option>
              {waves.map((w) => (
                <option key={w.id} value={w.id}>
                  {w.name}
                </option>
              ))}
            </select>
          </div>

          {/* Study Program Filter */}
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" style={{ fontSize: '0.8125rem' }}>
              Program Studi
            </label>
            <select
              className="form-select"
              value={programFilter}
              onChange={(e) => {
                setProgramFilter(e.target.value);
                setPage(1);
              }}
            >
              <option value="">Semua Program Studi</option>
              {studyPrograms.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name}
                </option>
              ))}
            </select>
          </div>

          {/* Reset Action */}
          <div>
            {isFilterActive && (
              <button
                onClick={handleResetFilters}
                className="btn btn-secondary btn-sm"
                style={{ width: '100%', height: '38px', gap: '6px' }}
              >
                <X size={16} />
                <span>Reset Filter</span>
              </button>
            )}
          </div>
        </div>

        {isFilterActive && (
          <div
            style={{
              marginTop: '12px',
              paddingTop: '8px',
              borderTop: '1px solid var(--color-border)',
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              fontSize: '0.75rem',
              color: 'var(--color-primary)',
            }}
          >
            <Filter size={14} />
            <span>Filter aktif diterapkan pada data pendaftaran.</span>
          </div>
        )}
      </div>

      {/* Applications Table */}
      <div className="card">
        {isLoading ? (
          <LoadingSpinner message="Mengambil data pendaftar..." />
        ) : filteredApplications.length === 0 ? (
          <EmptyState
            title="Tidak Ada Pendaftaran Ditemukan"
            description="Tidak ada pendaftaran yang sesuai dengan pencarian atau filter yang dipilih."
            action={
              isFilterActive ? (
                <button onClick={handleResetFilters} className="btn btn-secondary btn-sm">
                  Hapus Semua Filter
                </button>
              ) : undefined
            }
          />
        ) : (
          <>
            <div className="table-container">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>No. Pendaftaran</th>
                    <th>Calon Mahasiswa</th>
                    <th>Program Studi</th>
                    <th>Gelombang</th>
                    <th>Status</th>
                    <th>Waktu Pengajuan</th>
                    <th style={{ textAlign: 'right' }}>Aksi</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredApplications.map((app) => (
                    <tr key={app.id}>
                      <td style={{ fontWeight: 600, color: 'var(--color-primary)' }}>
                        {app.registration_number}
                      </td>
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
                          className="btn btn-primary btn-sm"
                        >
                          Periksa
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination Controls */}
            {meta && (
              <div
                style={{
                  marginTop: '20px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  fontSize: '0.875rem',
                  color: 'var(--color-text-secondary)',
                }}
              >
                <div>
                  Menampilkan {applications.length} dari total <strong>{meta.total}</strong> pendaftar
                  (Halaman {meta.current_page} dari {meta.last_page})
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button
                    className="btn btn-secondary btn-sm"
                    disabled={meta.current_page <= 1 || isLoading}
                    onClick={() => setPage((prev) => Math.max(prev - 1, 1))}
                  >
                    <ChevronLeft size={16} />
                    <span>Sebelumnya</span>
                  </button>
                  <button
                    className="btn btn-secondary btn-sm"
                    disabled={meta.current_page >= meta.last_page || isLoading}
                    onClick={() => setPage((prev) => prev + 1)}
                  >
                    <span>Berikutnya</span>
                    <ChevronRight size={16} />
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};
