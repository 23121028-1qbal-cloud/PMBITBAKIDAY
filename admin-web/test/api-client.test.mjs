import { test, describe } from 'node:test';
import assert from 'node:assert';

// Mock function matching extractApiError in src/api/client.ts
function extractApiError(error) {
  if (error && error.isAxiosError) {
    const responseData = error.response?.data;
    if (responseData) {
      return {
        code: responseData.error?.code || `HTTP_${error.response?.status || 'UNKNOWN'}`,
        message: responseData.message || error.message,
        details: responseData.error?.details,
      };
    }
    if (error.response?.status === 401) {
      return { code: 'UNAUTHENTICATED', message: 'Sesi Anda telah berakhir. Silakan login kembali.' };
    }
    if (error.response?.status === 403) {
      return { code: 'FORBIDDEN', message: 'Anda tidak memiliki hak akses untuk tindakan ini.' };
    }
    if (error.response?.status === 404) {
      return { code: 'RESOURCE_NOT_FOUND', message: 'Data yang diminta tidak ditemukan.' };
    }
    if (error.response?.status === 409) {
      return { code: 'STATE_CONFLICT', message: 'Status pendaftaran telah berubah. Silakan muat ulang halaman.' };
    }
    if (error.response?.status === 419) {
      return { code: 'CSRF_MISMATCH', message: 'Sesi CSRF kedaluwarsa. Silakan muat ulang halaman dan coba lagi.' };
    }
    return {
      code: 'NETWORK_ERROR',
      message: error.message || 'Gagal terhubung ke server backend.',
    };
  }
  return {
    code: 'CLIENT_ERROR',
    message: error instanceof Error ? error.message : 'Terjadi kesalahan tidak terduga.',
  };
}

describe('API Error Handling Contract', () => {
  test('should handle standard backend 409 STATE_CONFLICT error', () => {
    const error = {
      isAxiosError: true,
      response: {
        status: 409,
        data: {
          success: false,
          message: 'Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.',
          error: {
            code: 'STATE_CONFLICT',
          },
        },
      },
    };

    const result = extractApiError(error);
    assert.strictEqual(result.code, 'STATE_CONFLICT');
    assert.ok(result.message.includes('Status pendaftaran telah berubah'));
  });

  test('should handle 422 VALIDATION_ERROR with field details', () => {
    const error = {
      isAxiosError: true,
      response: {
        status: 422,
        data: {
          success: false,
          message: 'Validasi gagal.',
          error: {
            code: 'VALIDATION_ERROR',
            details: {
              note: ['Catatan wajib diisi minimal 3 karakter.'],
            },
          },
        },
      },
    };

    const result = extractApiError(error);
    assert.strictEqual(result.code, 'VALIDATION_ERROR');
    assert.ok(result.details?.note);
    assert.strictEqual(result.details.note[0], 'Catatan wajib diisi minimal 3 karakter.');
  });

  test('should handle fallback 401 UNAUTHENTICATED', () => {
    const error = {
      isAxiosError: true,
      response: {
        status: 401,
      },
    };

    const result = extractApiError(error);
    assert.strictEqual(result.code, 'UNAUTHENTICATED');
  });

  test('should handle fallback 403 FORBIDDEN', () => {
    const error = {
      isAxiosError: true,
      response: {
        status: 403,
      },
    };

    const result = extractApiError(error);
    assert.strictEqual(result.code, 'FORBIDDEN');
  });

  test('should handle fallback 419 CSRF_MISMATCH', () => {
    const error = {
      isAxiosError: true,
      response: {
        status: 419,
      },
    };

    const result = extractApiError(error);
    assert.strictEqual(result.code, 'CSRF_MISMATCH');
  });
});
