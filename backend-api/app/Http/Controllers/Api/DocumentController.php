<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Application;
use App\Models\ApplicationDocument;
use App\Support\ApiErrorResponse;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Symfony\Component\HttpFoundation\StreamedResponse;

class DocumentController extends Controller
{
    /**
     * POST /api/v1/applications/{id}/documents — unggah dokumen baru / ganti.
     */
    public function store(Request $request, int $id): JsonResponse
    {
        $app = $this->resolveApplication($request, $id, checkOwnership: true);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        if (! in_array($app->status, ['draft', 'revision'], true)) {
            return ApiErrorResponse::error(
                422,
                'APPLICATION_NOT_EDITABLE',
                'Dokumen tidak dapat diunggah pada status pendaftaran saat ini.',
            );
        }

        $validated = $request->validate([
            'document_type' => ['required', 'string', 'max:30'],
            'file' => ['required', 'file', 'mimes:pdf,jpg,jpeg,png', 'max:10240'], // 10MB limit
        ]);

        $file = $request->file('file');
        $ext = $file->getClientOriginalExtension() ?: 'bin';
        $fileName = sprintf('doc_%s_%s.%s', $validated['document_type'], uniqid(), $ext);
        $dir = 'documents/' . $app->id;
        $filePath = $file->storeAs($dir, $fileName, 'local');

        // Jika dokumen jenis ini sudah ada sebelumnya, hapus file lama dari disk
        $existing = ApplicationDocument::query()
            ->where('application_id', $app->id)
            ->where('document_type', $validated['document_type'])
            ->first();

        if ($existing && $existing->file_path && Storage::disk('local')->exists($existing->file_path)) {
            Storage::disk('local')->delete($existing->file_path);
        }

        $document = ApplicationDocument::updateOrCreate(
            [
                'application_id' => $app->id,
                'document_type' => $validated['document_type'],
            ],
            [
                'file_path' => $filePath,
                'mime_type' => $file->getMimeType() ?: 'application/octet-stream',
                'file_size' => $file->getSize(),
                'uploaded_at' => now(),
            ]
        );

        return ApiErrorResponse::success($this->payload($document), 'Dokumen berhasil diunggah.', 201);
    }

    /**
     * GET /api/v1/applications/{id}/documents — melihat daftar metadata dokumen.
     */
    public function index(Request $request, int $id): JsonResponse
    {
        $app = $this->resolveApplication($request, $id, checkOwnership: false);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        $documents = ApplicationDocument::query()
            ->where('application_id', $app->id)
            ->orderBy('id')
            ->get()
            ->map(fn (ApplicationDocument $doc) => $this->payload($doc))
            ->values()
            ->all();

        return ApiErrorResponse::success($documents, 'Daftar dokumen berhasil diambil.');
    }

    /**
     * GET /api/v1/applications/{id}/documents/{documentId}/download — mengunduh berkas privat.
     */
    public function download(Request $request, int $id, int $documentId): StreamedResponse|JsonResponse
    {
        $app = $this->resolveApplication($request, $id, checkOwnership: false);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        $document = ApplicationDocument::query()
            ->where('id', $documentId)
            ->where('application_id', $app->id)
            ->first();

        if (! $document || ! Storage::disk('local')->exists($document->file_path)) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Berkas dokumen tidak ditemukan.');
        }

        return Storage::disk('local')->download(
            $document->file_path,
            basename($document->file_path),
            ['Content-Type' => $document->mime_type],
        );
    }

    /**
     * DELETE /api/v1/applications/{id}/documents/{documentId} — menghapus dokumen.
     */
    public function destroy(Request $request, int $id, int $documentId): JsonResponse
    {
        $app = $this->resolveApplication($request, $id, checkOwnership: true);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        if (! in_array($app->status, ['draft', 'revision'], true)) {
            return ApiErrorResponse::error(
                422,
                'APPLICATION_NOT_EDITABLE',
                'Dokumen tidak dapat dihapus pada status pendaftaran saat ini.',
            );
        }

        $document = ApplicationDocument::query()
            ->where('id', $documentId)
            ->where('application_id', $app->id)
            ->first();

        if (! $document) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Dokumen tidak ditemukan.');
        }

        if (Storage::disk('local')->exists($document->file_path)) {
            Storage::disk('local')->delete($document->file_path);
        }

        $document->delete();

        return ApiErrorResponse::success(null, 'Dokumen berhasil dihapus.');
    }

    private function resolveApplication(Request $request, int $id, bool $checkOwnership): ?Application
    {
        $user = $request->user();
        $app = Application::query()->find($id);

        if (! $app) {
            return null;
        }

        $isStaff = $user->hasRole(['admin', 'panitia', 'pimpinan']);

        if ($checkOwnership || ! $isStaff) {
            $profile = $user->applicantProfile;
            if (! $profile || $app->applicant_id !== $profile->id) {
                return null;
            }
        }

        return $app;
    }

    private function payload(ApplicationDocument $document): array
    {
        return [
            'id' => $document->id,
            'application_id' => $document->application_id,
            'document_type' => $document->document_type,
            'file_name' => basename($document->file_path),
            'file_size' => (int) $document->file_size,
            'uploaded_at' => $document->uploaded_at?->toIso8601String(),
        ];
    }
}
