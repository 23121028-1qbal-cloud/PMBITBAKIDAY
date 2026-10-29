import axios, { AxiosError } from 'axios';
import { ApiErrorResponse } from '../types';

// In development, Vite reverse-proxies /api and /sanctum to Laravel backend.
// In production or custom env, VITE_API_BASE_URL can point to full URL.
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest',
  },
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
});

// Helper to bootstrap Sanctum CSRF cookie before authentication or mutations
export async function bootstrapCsrf(): Promise<void> {
  const sanctumBase = import.meta.env.VITE_SANCTUM_BASE_URL || '';
  await axios.get(`${sanctumBase}/sanctum/csrf-cookie`, {
    withCredentials: true,
    headers: {
      'Accept': 'application/json',
      'X-Requested-With': 'XMLHttpRequest',
    },
  });
}

// Helper to extract clean error message and error code from Axios response
export function extractApiError(error: unknown): { code: string; message: string; details?: Record<string, string[]> } {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ApiErrorResponse>;
    const responseData = axiosError.response?.data;

    if (responseData) {
      return {
        code: responseData.error?.code || `HTTP_${axiosError.response?.status || 'UNKNOWN'}`,
        message: responseData.message || axiosError.message,
        details: responseData.error?.details,
      };
    }

    if (axiosError.response?.status === 401) {
      return { code: 'UNAUTHENTICATED', message: 'Sesi Anda telah berakhir. Silakan login kembali.' };
    }
    if (axiosError.response?.status === 403) {
      return { code: 'FORBIDDEN', message: 'Anda tidak memiliki hak akses untuk tindakan ini.' };
    }
    if (axiosError.response?.status === 404) {
      return { code: 'RESOURCE_NOT_FOUND', message: 'Data yang diminta tidak ditemukan.' };
    }
    if (axiosError.response?.status === 409) {
      return { code: 'STATE_CONFLICT', message: 'Status pendaftaran telah berubah. Silakan muat ulang halaman.' };
    }
    if (axiosError.response?.status === 419) {
      return { code: 'CSRF_MISMATCH', message: 'Sesi CSRF kedaluwarsa. Silakan muat ulang halaman dan coba lagi.' };
    }

    return {
      code: 'NETWORK_ERROR',
      message: axiosError.message || 'Gagal terhubung ke server backend.',
    };
  }

  return {
    code: 'CLIENT_ERROR',
    message: error instanceof Error ? error.message : 'Terjadi kesalahan tidak terduga.',
  };
}
