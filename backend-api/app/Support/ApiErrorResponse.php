<?php

namespace App\Support;

use App\Exceptions\AccountInactiveException;
use Illuminate\Auth\Access\AuthorizationException;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Database\QueryException;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Arr;
use Illuminate\Validation\ValidationException;
use Symfony\Component\HttpKernel\Exception\HttpExceptionInterface;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\HttpKernel\Exception\TokenMismatchException;
use Throwable;

class ApiErrorResponse
{
    /**
     * Build the standard error envelope:
     * {"success": false, "message": "...", "error": {"code": "...", "details": {}}}
     */
    public static function error(
        int $status,
        string $code,
        string $message,
        array $details = [],
    ): JsonResponse {
        $error = ['code' => $code];

        if ($details !== []) {
            $error['details'] = $details;
        }

        return response()->json([
            'success' => false,
            'message' => $message,
            'error' => $error,
        ], $status);
    }

    /**
     * Build the standard success envelope:
     * {"success": true, "message": "...", "data": {...}}
     */
    public static function success(
        mixed $data = null,
        string $message = 'Permintaan berhasil diproses.',
        int $status = 200,
    ): JsonResponse {
        return response()->json([
            'success' => true,
            'message' => $message,
            'data' => $data,
        ], $status);
    }

    /**
     * Map a thrown exception onto the documented HTTP status + error code.
     *
     * Returns null when the request is not an API request so that Laravel's
     * default HTML rendering remains untouched.
     */
    public static function fromThrowable(Throwable $e): ?JsonResponse
    {
        if ($e instanceof ValidationException) {
            if ($e->status === 401) {
                return self::error(
                    401,
                    'INVALID_CREDENTIALS',
                    'Email atau password tidak valid.',
                );
            }

            return self::error(
                422,
                'VALIDATION_ERROR',
                'Periksa kembali data yang dimasukkan.',
                $e->errors(),
            );
        }

        if ($e instanceof AuthenticationException) {
            return self::error(401, 'UNAUTHENTICATED', 'Silakan login terlebih dahulu.');
        }

        if ($e instanceof AccountInactiveException) {
            return self::error(
                403,
                'ACCOUNT_INACTIVE',
                'Akun tidak aktif. Silakan hubungi administrator.',
            );
        }

        if ($e instanceof AuthorizationException) {
            return self::error(
                403,
                'FORBIDDEN',
                'Anda tidak memiliki izin untuk melakukan tindakan ini.',
            );
        }

        if ($e instanceof TokenMismatchException || self::status($e) === 419) {
            return self::error(
                419,
                'CSRF_TOKEN_MISMATCH',
                'Sesi keamanan tidak valid. Muat ulang halaman dan coba kembali.',
            );
        }

        if ($e instanceof NotFoundHttpException) {
            return self::error(404, 'RESOURCE_NOT_FOUND', 'Data yang diminta tidak ditemukan.');
        }

        if ($e instanceof QueryException) {
            return self::fromQueryException($e);
        }

        $status = self::status($e);

        if ($status !== null && $status >= 400 && $status < 500) {
            return self::error(
                $status,
                self::codeForStatus($status),
                self::messageForStatus($status),
            );
        }

        // Unknown/internal failure: never leak stack traces or SQL to clients.
        return self::error(
            500,
            'INTERNAL_SERVER_ERROR',
            'Terjadi kesalahan pada server. Silakan coba kembali nanti.',
        );
    }

    private static function fromQueryException(QueryException $e): JsonResponse
    {
        $sqlState = (string) $e->getCode();
        $sql = (string) $e->getSql();
        $message = (string) $e->getMessage();

        $isUnique = $sqlState === '23000'
            || $sqlState === '23505'
            || str_contains($message, 'UNIQUE constraint failed')
            || str_contains($message, 'Duplicate entry');

        if ($isUnique && str_contains(strtolower($sql), 'applications')) {
            return self::error(
                409,
                'APPLICATION_ALREADY_EXISTS',
                'Anda sudah memiliki pengajuan pada gelombang ini.',
            );
        }

        // Integrity problems that are not the documented duplicate-application
        // conflict are treated as internal errors; SQL text is never returned.
        return self::error(
            500,
            'INTERNAL_SERVER_ERROR',
            'Terjadi kesalahan pada server. Silakan coba kembali nanti.',
        );
    }

    private static function status(Throwable $e): ?int
    {
        return $e instanceof HttpExceptionInterface ? $e->getStatusCode() : null;
    }

    private static function codeForStatus(int $status): string
    {
        return match ($status) {
            400 => 'BAD_REQUEST',
            401 => 'UNAUTHENTICATED',
            403 => 'FORBIDDEN',
            404 => 'RESOURCE_NOT_FOUND',
            405 => 'METHOD_NOT_ALLOWED',
            409 => 'STATE_CONFLICT',
            413 => 'FILE_TOO_LARGE',
            415 => 'UNSUPPORTED_FILE_TYPE',
            419 => 'CSRF_TOKEN_MISMATCH',
            422 => 'VALIDATION_ERROR',
            429 => 'RATE_LIMIT_EXCEEDED',
            503 => 'SERVICE_UNAVAILABLE',
            default => $status >= 500 ? 'INTERNAL_SERVER_ERROR' : 'BAD_REQUEST',
        };
    }

    private static function messageForStatus(int $status): string
    {
        return match ($status) {
            400 => 'Permintaan tidak dapat diproses.',
            401 => 'Silakan login terlebih dahulu.',
            403 => 'Anda tidak memiliki izin untuk melakukan tindakan ini.',
            404 => 'Data yang diminta tidak ditemukan.',
            405 => 'Metode request tidak didukung.',
            409 => 'Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.',
            413 => 'Ukuran file melebihi batas yang diizinkan.',
            415 => 'Format file tidak didukung.',
            419 => 'Sesi keamanan tidak valid. Muat ulang halaman dan coba kembali.',
            422 => 'Periksa kembali data yang dimasukkan.',
            429 => 'Terlalu banyak permintaan. Silakan coba kembali nanti.',
            503 => 'Layanan sementara tidak tersedia. Silakan coba kembali nanti.',
            default => $status >= 500
                ? 'Terjadi kesalahan pada server. Silakan coba kembali nanti.'
                : 'Permintaan tidak dapat diproses.',
        };
    }
}
