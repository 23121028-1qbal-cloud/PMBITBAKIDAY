import React, { useEffect, useState, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { fetchAdminApplicationDetail } from '../api/applications';
import {
  submitStartReview,
  submitRequestRevision,
  submitDecision,
} from '../api/reviews';
import { downloadApplicationDocument } from '../api/documents';
import { ApplicationDetail } from '../types';
import { extractApiError } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { StatusBadge } from '../components/StatusBadge';
import { Modal } from '../components/Modal';
import { Alert } from '../components/Alert';
import { LoadingSpinner } from '../components/LoadingSpinner';
import {
  ArrowLeft,
  Download,
  FileText,
  User,
  GraduationCap,
  Calendar,
  CheckCircle,
  XCircle,
  FileEdit,
  History,
  AlertTriangle,
  Loader2,
} from 'lucide-react';

export const ApplicationDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const applicationId = Number(id);
  const { hasRole } = useAuth();

  const [application, setApplication] = useState<ApplicationDetail | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Action modals state
  const [isRevisionModalOpen, setIsRevisionModalOpen] = useState<boolean>(false);
  const [isDecisionModalOpen, setIsDecisionModalOpen] = useState<boolean>(false);
  const [revisionNote, setRevisionNote] = useState<string>('');
  const [decisionType, setDecisionType] = useState<'accepted' | 'rejected'>('accepted');
  const [decisionNote, setDecisionNote] = useState<string>('');
  const [isSubmittingAction, setIsSubmittingAction] = useState<boolean>(false);
  const [downloadingDocId, setDownloadingDocId] = useState<number | null>(null);

  const canManage = hasRole(['admin', 'panitia']);

  const loadDetail = useCallback(async () => {
    if (!applicationId) return;
    try {
      setIsLoading(true);
      setErrorMsg(null);
      const res = await fetchAdminApplicationDetail(applicationId);
      setApplication(res.data);
    } catch (err) {
      const error = extractApiError(err);
      setErrorMsg(error.message);
    } finally {
      setIsLoading(false);
    }
  }, [applicationId]);

  useEffect(() => {
    loadDetail();
  }, [loadDetail]);

  // Handler for starting review (submitted -> review)
  const handleStartReview = async () => {
    if (!application) return;
    if (!window.confirm('Mulai proses pemeriksaan berkas untuk pendaftar ini?')) return;

    try {
      setIsSubmittingAction(true);
      setErrorMsg(null);
      setSuccessMsg(null);

      await submitStartReview(application.id, {
        expected_status: application.status,
      });

      setSuccessMsg('Pemeriksaan berkas pendaftaran telah dimulai.');
      await loadDetail();
    } catch (err) {
      const error = extractApiError(err);
      if (error.code === 'STATE_CONFLICT') {
        setErrorMsg('Status pendaftaran telah berubah di server. Halaman dimuat ulang.');
        await loadDetail();
      } else {
        setErrorMsg(error.message);
      }
    } finally {
      setIsSubmittingAction(false);
    }
  };

  // Handler for requesting revision (review -> revision)
  const handleRequestRevision = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!application) return;

    if (!revisionNote.trim() || revisionNote.trim().length < 3) {
      setErrorMsg('Catatan revisi wajib diisi minimal 3 karakter.');
      return;
    }

    try {
      setIsSubmittingAction(true);
      setErrorMsg(null);
      setSuccessMsg(null);

      await submitRequestRevision(application.id, {
        expected_status: application.status,
        note: revisionNote.trim(),
      });

      setSuccessMsg('Permintaan revisi berkas berhasil dikirim ke calon mahasiswa.');
      setIsRevisionModalOpen(false);
      setRevisionNote('');
      await loadDetail();
    } catch (err) {
      const error = extractApiError(err);
      if (error.code === 'STATE_CONFLICT') {
        setErrorMsg('Status pendaftaran telah berubah di server. Halaman dimuat ulang.');
        setIsRevisionModalOpen(false);
        await loadDetail();
      } else {
        setErrorMsg(error.message);
      }
    } finally {
      setIsSubmittingAction(false);
    }
  };

  // Handler for decision (review -> accepted / rejected)
  const handleDecision = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!application) return;

    if (!decisionNote.trim() || decisionNote.trim().length < 3) {
      setErrorMsg('Catatan keputusan wajib diisi minimal 3 karakter.');
      return;
    }

    try {
      setIsSubmittingAction(true);
      setErrorMsg(null);
      setSuccessMsg(null);

      await submitDecision(application.id, {
        expected_status: application.status,
        decision: decisionType,
        note: decisionNote.trim(),
      });

      setSuccessMsg(
        `Keputusan seleksi berhasil ditetapkan: ${
          decisionType === 'accepted' ? 'DITERIMA' : 'DITOLAK'
        }.`
      );
      setIsDecisionModalOpen(false);
      setDecisionNote('');
      await loadDetail();
    } catch (err) {
      const error = extractApiError(err);
      if (error.code === 'STATE_CONFLICT') {
        setErrorMsg('Status pendaftaran telah berubah di server. Halaman dimuat ulang.');
        setIsDecisionModalOpen(false);
        await loadDetail();
      } else {
        setErrorMsg(error.message);
      }
    } finally {
      setIsSubmittingAction(false);
    }
  };

  // Handler for document download
  const handleDownloadDocument = async (docId: number, preferredName: string) => {
    try {
      setDownloadingDocId(docId);
      await downloadApplicationDocument(applicationId, docId, preferredName);
    } catch (err) {
      const error = extractApiError(err);
      alert(`Gagal mengunduh dokumen: ${error.message}`);
    } finally {
      setDownloadingDocId(null);
    }
  };

  const formatFileSize = (bytes: number): string => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
  };

  if (isLoading) {
    return <LoadingSpinner message="Mengambil detail berkas pendaftaran..." />;
  }

  if (!application) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '48px' }}>
        <AlertTriangle size={36} style={{ color: 'var(--color-danger)', margin: '0 auto 16px' }} />
        <h2>Data Pendaftaran Tidak Ditemukan</h2>
        <p style={{ color: 'var(--color-text-secondary)', marginTop: '8px' }}>
          Data pendaftaran dengan ID #{applicationId} tidak tersedia pada sistem.
        </p>
        <Link to="/applications" className="btn btn-secondary" style={{ marginTop: '20px' }}>
          Kembali ke Daftar Pendaftar
        </Link>
      </div>
    );
  }

  return (
    <div>
      {/* Top Breadcrumb & Action Header */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '20px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Link to="/applications" className="btn btn-secondary btn-sm">
            <ArrowLeft size={16} />
            <span>Kembali</span>
          </Link>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <h1 style={{ fontSize: '1.375rem', fontWeight: 700 }}>
                Pengajuan #{application.registration_number}
              </h1>
              <StatusBadge status={application.status} />
            </div>
            <div style={{ fontSize: '0.8125rem', color: 'var(--color-text-secondary)', marginTop: '2px' }}>
              Diajukan pada:{' '}
              {application.submitted_at
                ? new Date(application.submitted_at).toLocaleString('id-ID', {
                    dateStyle: 'long',
                    timeStyle: 'short',
                  })
                : '-'}
            </div>
          </div>
        </div>

        {/* Action Buttons for Review / Revision / Decision */}
        {canManage && (
          <div style={{ display: 'flex', gap: '10px' }}>
            {application.status === 'submitted' && (
              <button
                onClick={handleStartReview}
                disabled={isSubmittingAction}
                className="btn btn-primary"
              >
                {isSubmittingAction ? (
                  <Loader2 size={16} className="animate-spin" />
                ) : (
                  <FileText size={16} />
                )}
                <span>Mulai Pemeriksaan</span>
              </button>
            )}

            {application.status === 'review' && (
              <>
                <button
                  onClick={() => setIsRevisionModalOpen(true)}
                  disabled={isSubmittingAction}
                  className="btn btn-accent"
                >
                  <FileEdit size={16} />
                  <span>Minta Revisi</span>
                </button>
                <button
                  onClick={() => setIsDecisionModalOpen(true)}
                  disabled={isSubmittingAction}
                  className="btn btn-primary"
                >
                  <CheckCircle size={16} />
                  <span>Tetapkan Keputusan</span>
                </button>
              </>
            )}
          </div>
        )}
      </div>

      {errorMsg && (
        <Alert type="danger" message={errorMsg} onClose={() => setErrorMsg(null)} />
      )}
      {successMsg && (
        <Alert type="success" message={successMsg} onClose={() => setSuccessMsg(null)} />
      )}

      {/* Grid: Applicant Info & Study Program */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
          gap: '20px',
          marginBottom: '20px',
        }}
      >
        {/* Applicant Profile Card */}
        <div className="card" style={{ marginBottom: 0 }}>
          <h2 className="card-title">
            <User size={18} style={{ color: 'var(--color-primary)' }} />
            <span>Data Calon Mahasiswa</span>
          </h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', fontSize: '0.875rem' }}>
            <div>
              <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                Nama Lengkap
              </div>
              <div style={{ fontWeight: 600 }}>{application.applicant?.full_name || '-'}</div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <div>
                <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>NIK</div>
                <div style={{ fontWeight: 500 }}>{application.applicant?.nik || '-'}</div>
              </div>
              <div>
                <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                  Jenis Kelamin
                </div>
                <div style={{ fontWeight: 500 }}>
                  {application.applicant?.gender === 'L' ||
                  application.applicant?.gender === 'Laki-laki'
                    ? 'Laki-laki'
                    : application.applicant?.gender === 'P' ||
                      application.applicant?.gender === 'Perempuan'
                    ? 'Perempuan'
                    : application.applicant?.gender || '-'}
                </div>
              </div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <div>
                <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                  Tempat / Tanggal Lahir
                </div>
                <div style={{ fontWeight: 500 }}>
                  {application.applicant?.birth_place || '-'},{' '}
                  {application.applicant?.birth_date || '-'}
                </div>
              </div>
              <div>
                <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                  No. Telepon / WhatsApp
                </div>
                <div style={{ fontWeight: 500 }}>{application.applicant?.phone || '-'}</div>
              </div>
            </div>
            <div>
              <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                Alamat Domisili
              </div>
              <div style={{ fontWeight: 500 }}>{application.applicant?.address || '-'}</div>
            </div>
          </div>
        </div>

        {/* Application Details Card */}
        <div className="card" style={{ marginBottom: 0 }}>
          <h2 className="card-title">
            <GraduationCap size={18} style={{ color: 'var(--color-primary)' }} />
            <span>Pilihan Program Studi & Gelombang</span>
          </h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', fontSize: '0.875rem' }}>
            <div>
              <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                Program Studi Pilihan
              </div>
              <div style={{ fontWeight: 600, fontSize: '1rem', color: 'var(--color-primary)' }}>
                {application.study_program?.name || '-'}
              </div>
            </div>
            <div>
              <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                Gelombang Penerimaan
              </div>
              <div style={{ fontWeight: 500 }}>{application.wave?.name || '-'}</div>
            </div>
            <div>
              <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                Status Saat Ini
              </div>
              <div style={{ marginTop: '4px' }}>
                <StatusBadge status={application.status} />
              </div>
            </div>
            <div>
              <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
                Waktu Submit
              </div>
              <div style={{ fontWeight: 500 }}>
                {application.submitted_at
                  ? new Date(application.submitted_at).toLocaleString('id-ID')
                  : '-'}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Section: Documents List */}
      <div className="card">
        <h2 className="card-title">
          <FileText size={18} style={{ color: 'var(--color-primary)' }} />
          <span>Berkas Dokumen Pendaftaran</span>
        </h2>
        {application.documents.length === 0 ? (
          <div style={{ padding: '20px', textAlign: 'center', color: 'var(--color-text-secondary)' }}>
            Belum ada berkas dokumen yang diunggah oleh calon mahasiswa.
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Jenis Dokumen</th>
                  <th>Nama File</th>
                  <th>Ukuran File</th>
                  <th>Waktu Unggah</th>
                  <th style={{ textAlign: 'right' }}>Aksi</th>
                </tr>
              </thead>
              <tbody>
                {application.documents.map((doc) => (
                  <tr key={doc.id}>
                    <td style={{ fontWeight: 600, textTransform: 'capitalize' }}>
                      {doc.document_type.replace(/_/g, ' ')}
                    </td>
                    <td style={{ color: 'var(--color-text-secondary)' }}>{doc.file_name}</td>
                    <td>{formatFileSize(doc.file_size)}</td>
                    <td style={{ fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>
                      {doc.uploaded_at
                        ? new Date(doc.uploaded_at).toLocaleString('id-ID')
                        : '-'}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <button
                        onClick={() => handleDownloadDocument(doc.id, doc.file_name)}
                        disabled={downloadingDocId === doc.id}
                        className="btn btn-secondary btn-sm"
                        style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}
                      >
                        {downloadingDocId === doc.id ? (
                          <Loader2 size={14} className="animate-spin" />
                        ) : (
                          <Download size={14} />
                        )}
                        <span>Unduh Berkas</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Section: Status History Timeline */}
      <div className="card">
        <h2 className="card-title">
          <History size={18} style={{ color: 'var(--color-primary)' }} />
          <span>Riwayat Perubahan Status Pendaftaran</span>
        </h2>
        {application.status_histories.length === 0 ? (
          <div style={{ padding: '20px', textAlign: 'center', color: 'var(--color-text-secondary)' }}>
            Belum ada riwayat perubahan status tercatat.
          </div>
        ) : (
          <div className="timeline">
            {application.status_histories.map((hist, idx) => (
              <div key={idx} className="timeline-item">
                <div className="timeline-dot" />
                <div className="timeline-content">
                  <div className="timeline-time">
                    <Calendar size={12} style={{ display: 'inline', marginRight: '4px' }} />
                    {hist.changed_at
                      ? new Date(hist.changed_at).toLocaleString('id-ID', {
                          dateStyle: 'medium',
                          timeStyle: 'short',
                        })
                      : '-'}
                  </div>
                  <div className="timeline-title">
                    Transisi:{' '}
                    <span style={{ textTransform: 'capitalize' }}>
                      {hist.from_status || 'Awal'}
                    </span>{' '}
                    ➔{' '}
                    <span style={{ color: 'var(--color-primary)', textTransform: 'capitalize' }}>
                      {hist.to_status}
                    </span>
                  </div>
                  {hist.note && (
                    <div className="timeline-note">
                      <strong>Catatan:</strong> {hist.note}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal: Request Revision */}
      <Modal
        isOpen={isRevisionModalOpen}
        onClose={() => !isSubmittingAction && setIsRevisionModalOpen(false)}
        title="Minta Perbaikan / Revisi Berkas"
        footer={
          <>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsRevisionModalOpen(false)}
              disabled={isSubmittingAction}
            >
              Batal
            </button>
            <button
              type="button"
              className="btn btn-accent"
              onClick={handleRequestRevision}
              disabled={isSubmittingAction || !revisionNote.trim()}
            >
              {isSubmittingAction ? 'Menyimpan...' : 'Kirim Permintaan Revisi'}
            </button>
          </>
        }
      >
        <form onSubmit={handleRequestRevision}>
          <p style={{ fontSize: '0.875rem', color: 'var(--color-text-secondary)', marginBottom: '16px' }}>
            Status pendaftaran akan diubah menjadi <strong>Perlu Revisi (revision)</strong>.
            Calon mahasiswa akan mendapatkan pemberitahuan perbaikan berkas.
          </p>
          <div className="form-group">
            <label className="form-label" htmlFor="revisionNote">
              Catatan Instruksi Revisi (Wajib)
            </label>
            <textarea
              id="revisionNote"
              rows={4}
              className="form-textarea"
              placeholder="Jelaskan berkas apa yang perlu diperbaiki (misal: scan ijazah buram / KTP terpotong)..."
              value={revisionNote}
              onChange={(e) => setRevisionNote(e.target.value)}
              disabled={isSubmittingAction}
              required
            />
          </div>
        </form>
      </Modal>

      {/* Modal: Decision */}
      <Modal
        isOpen={isDecisionModalOpen}
        onClose={() => !isSubmittingAction && setIsDecisionModalOpen(false)}
        title="Penetapan Keputusan Seleksi"
        footer={
          <>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsDecisionModalOpen(false)}
              disabled={isSubmittingAction}
            >
              Batal
            </button>
            <button
              type="button"
              className={`btn ${decisionType === 'accepted' ? 'btn-primary' : 'btn-danger'}`}
              onClick={handleDecision}
              disabled={isSubmittingAction || !decisionNote.trim()}
            >
              {isSubmittingAction ? 'Menyimpan...' : 'Simpan Keputusan'}
            </button>
          </>
        }
      >
        <form onSubmit={handleDecision}>
          <div className="form-group">
            <label className="form-label">Hasil Keputusan Seleksi</label>
            <div style={{ display: 'flex', gap: '16px', marginTop: '8px' }}>
              <label
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  cursor: 'pointer',
                  padding: '10px 16px',
                  borderRadius: '6px',
                  border: `2px solid ${
                    decisionType === 'accepted' ? 'var(--color-success)' : 'var(--color-border)'
                  }`,
                  backgroundColor: decisionType === 'accepted' ? 'var(--color-success-bg)' : '#FFF',
                  flex: 1,
                }}
              >
                <input
                  type="radio"
                  name="decision"
                  value="accepted"
                  checked={decisionType === 'accepted'}
                  onChange={() => setDecisionType('accepted')}
                  disabled={isSubmittingAction}
                />
                <CheckCircle size={18} style={{ color: 'var(--color-success)' }} />
                <span style={{ fontWeight: 600 }}>DITERIMA</span>
              </label>

              <label
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  cursor: 'pointer',
                  padding: '10px 16px',
                  borderRadius: '6px',
                  border: `2px solid ${
                    decisionType === 'rejected' ? 'var(--color-danger)' : 'var(--color-border)'
                  }`,
                  backgroundColor: decisionType === 'rejected' ? 'var(--color-danger-bg)' : '#FFF',
                  flex: 1,
                }}
              >
                <input
                  type="radio"
                  name="decision"
                  value="rejected"
                  checked={decisionType === 'rejected'}
                  onChange={() => setDecisionType('rejected')}
                  disabled={isSubmittingAction}
                />
                <XCircle size={18} style={{ color: 'var(--color-danger)' }} />
                <span style={{ fontWeight: 600 }}>DITOLAK</span>
              </label>
            </div>
          </div>

          <div className="form-group" style={{ marginTop: '16px' }}>
            <label className="form-label" htmlFor="decisionNote">
              Catatan Keputusan Seleksi (Wajib)
            </label>
            <textarea
              id="decisionNote"
              rows={4}
              className="form-textarea"
              placeholder="Berikan catatan resmi atau alasan penetapan keputusan..."
              value={decisionNote}
              onChange={(e) => setDecisionNote(e.target.value)}
              disabled={isSubmittingAction}
              required
            />
          </div>
        </form>
      </Modal>
    </div>
  );
};
