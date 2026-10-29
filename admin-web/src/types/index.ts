export type UserRole = 'admin' | 'panitia' | 'pimpinan' | 'calon_mahasiswa';

export interface User {
  id: number;
  email: string;
  role: UserRole;
}

export type ApplicationStatus =
  | 'draft'
  | 'submitted'
  | 'review'
  | 'revision'
  | 'resubmitted'
  | 'accepted'
  | 'rejected';

export interface AdmissionWave {
  id: number;
  name: string;
  start_date?: string;
  end_date?: string;
  is_active?: boolean;
}

export interface StudyProgram {
  id: number;
  name: string;
  code?: string;
  is_active?: boolean;
}

export interface ApplicantProfile {
  id: number;
  user_id?: number;
  full_name: string;
  nik: string;
  birth_place?: string;
  birth_date?: string;
  gender?: string;
  phone?: string;
  address?: string;
}

export interface ApplicationDocument {
  id: number;
  application_id?: number;
  document_type: string;
  file_name: string;
  file_size: number;
  uploaded_at?: string;
}

export interface StatusHistory {
  from_status: string;
  to_status: string;
  note?: string;
  changed_at?: string;
}

export interface ApplicationSummary {
  id: number;
  registration_number: string;
  applicant: {
    full_name: string;
    nik: string;
  } | null;
  wave: {
    id?: number;
    name?: string;
  } | null;
  study_program: {
    id?: number;
    name?: string;
  } | null;
  status: ApplicationStatus;
  submitted_at: string | null;
}

export interface ApplicationDetail {
  id: number;
  registration_number: string;
  status: ApplicationStatus;
  submitted_at: string | null;
  wave: {
    id?: number;
    name?: string;
  } | null;
  study_program: {
    id?: number;
    name?: string;
  } | null;
  applicant: ApplicantProfile | null;
  documents: ApplicationDocument[];
  status_histories: StatusHistory[];
}

export interface PaginationMeta {
  current_page: number;
  per_page: number;
  total: number;
  last_page: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  meta?: PaginationMeta;
}

export interface ApiErrorResponse {
  success: false;
  message: string;
  error?: {
    code: string;
    details?: Record<string, string[]>;
  };
}
