<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\AdmissionWave;
use App\Models\Application;
use App\Models\StudyProgram;
use App\Support\ApiErrorResponse;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ApplicationController extends Controller
{
    /**
     * POST /api/v1/applications — membuat draft pengajuan baru.
     */
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'wave_id' => ['required', 'exists:admission_waves,id'],
            'study_program_id' => ['required', 'exists:study_programs,id'],
        ]);

        $user = $request->user();
        $profile = $user->applicantProfile;

        if (! $profile) {
            return ApiErrorResponse::error(
                422,
                'APPLICATION_INCOMPLETE',
                'Profil calon mahasiswa belum lengkap.',
            );
        }

        $wave = AdmissionWave::query()
            ->where('id', $validated['wave_id'])
            ->where('is_active', true)
            ->where('end_date', '>=', now())
            ->first();

        if (! $wave) {
            return ApiErrorResponse::error(
                422,
                'WAVE_NOT_AVAILABLE',
                'Gelombang pendaftaran tidak aktif atau telah berakhir.',
            );
        }

        $program = StudyProgram::query()
            ->where('id', $validated['study_program_id'])
            ->where('is_active', true)
            ->first();

        if (! $program) {
            return ApiErrorResponse::error(
                422,
                'STUDY_PROGRAM_NOT_AVAILABLE',
                'Program studi yang dipilih tidak aktif.',
            );
        }

        $existing = Application::query()
            ->where('applicant_id', $profile->id)
            ->where('wave_id', $validated['wave_id'])
            ->first();

        if ($existing) {
            return ApiErrorResponse::error(
                409,
                'APPLICATION_ALREADY_EXISTS',
                'Anda sudah memiliki pengajuan pada gelombang ini.',
            );
        }

        $count = Application::query()->where('wave_id', $validated['wave_id'])->count();
        $seq = $count + 1;
        $regNumber = sprintf('PMB-%s-%06d', now()->format('Y'), $seq);

        while (Application::query()->where('registration_number', $regNumber)->exists()) {
            $seq++;
            $regNumber = sprintf('PMB-%s-%06d', now()->format('Y'), $seq);
        }

        $application = DB::transaction(function () use ($profile, $validated, $regNumber, $user) {
            $app = Application::create([
                'applicant_id' => $profile->id,
                'wave_id' => $validated['wave_id'],
                'study_program_id' => $validated['study_program_id'],
                'registration_number' => $regNumber,
                'status' => 'draft',
                'submitted_at' => null,
            ]);

            $app->statusHistories()->create([
                'from_status' => null,
                'to_status' => 'draft',
                'note' => 'Draft pendaftaran dibuat.',
                'changed_by_user_id' => $user->id,
            ]);

            return $app;
        });

        return ApiErrorResponse::success([
            'id' => $application->id,
            'registration_number' => $application->registration_number,
            'wave_id' => $application->wave_id,
            'study_program_id' => $application->study_program_id,
            'status' => $application->status,
            'submitted_at' => null,
        ], 'Draft pendaftaran berhasil dibuat.', 201);
    }

    /**
     * GET /api/v1/applications — daftar pengajuan milik sendiri.
     */
    public function index(Request $request): JsonResponse
    {
        $profile = $request->user()->applicantProfile;

        if (! $profile) {
            return response()->json([
                'success' => true,
                'message' => 'Daftar pengajuan berhasil diambil.',
                'data' => [],
                'meta' => [
                    'current_page' => 1,
                    'per_page' => 20,
                    'total' => 0,
                    'last_page' => 1,
                ],
            ], 200);
        }

        $perPage = (int) $request->input('per_page', 20);
        $perPage = min(max($perPage, 1), 100);
        $page = (int) $request->input('page', 1);
        $page = max($page, 1);

        $query = Application::query()
            ->where('applicant_id', $profile->id)
            ->with(['wave', 'studyProgram'])
            ->orderByDesc('created_at');

        $total = $query->count();
        $applications = $query->offset(($page - 1) * $perPage)->limit($perPage)->get();

        $data = $applications->map(fn (Application $app) => [
            'id' => $app->id,
            'registration_number' => $app->registration_number,
            'wave' => [
                'id' => $app->wave?->id,
                'name' => $app->wave?->name,
            ],
            'study_program' => [
                'id' => $app->studyProgram?->id,
                'name' => $app->studyProgram?->name,
            ],
            'status' => $app->status,
            'submitted_at' => $app->submitted_at?->toIso8601String(),
        ])->values()->all();

        return response()->json([
            'success' => true,
            'message' => 'Daftar pengajuan berhasil diambil.',
            'data' => $data,
            'meta' => [
                'current_page' => $page,
                'per_page' => $perPage,
                'total' => $total,
                'last_page' => (int) ceil($total / max($perPage, 1)),
            ],
        ], 200);
    }

    /**
     * GET /api/v1/applications/{id} — melihat detail pengajuan.
     */
    public function show(Request $request, int $id): JsonResponse
    {
        $user = $request->user();
        $app = Application::query()->with(['wave', 'studyProgram', 'applicant'])->find($id);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        if (! $user->hasRole(['admin', 'panitia', 'pimpinan'])) {
            $profile = $user->applicantProfile;
            if (! $profile || $app->applicant_id !== $profile->id) {
                return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
            }
        }

        return ApiErrorResponse::success([
            'id' => $app->id,
            'registration_number' => $app->registration_number,
            'wave' => [
                'id' => $app->wave?->id,
                'name' => $app->wave?->name,
            ],
            'study_program' => [
                'id' => $app->studyProgram?->id,
                'name' => $app->studyProgram?->name,
            ],
            'status' => $app->status,
            'submitted_at' => $app->submitted_at?->toIso8601String(),
            'applicant' => $app->applicant ? [
                'full_name' => $app->applicant->full_name,
                'nik' => $app->applicant->nik,
            ] : null,
        ], 'Detail pengajuan berhasil diambil.');
    }

    /**
     * PUT /api/v1/applications/{id} — memperbarui draft pengajuan.
     */
    public function update(Request $request, int $id): JsonResponse
    {
        $user = $request->user();
        $profile = $user->applicantProfile;

        $app = Application::query()
            ->where('id', $id)
            ->where('applicant_id', $profile?->id)
            ->first();

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        if ($app->status !== 'draft') {
            return ApiErrorResponse::error(
                422,
                'APPLICATION_NOT_EDITABLE',
                'Pengajuan tidak dapat diedit pada status saat ini.',
            );
        }

        $validated = $request->validate([
            'study_program_id' => ['required', 'exists:study_programs,id'],
        ]);

        $program = StudyProgram::query()
            ->where('id', $validated['study_program_id'])
            ->where('is_active', true)
            ->first();

        if (! $program) {
            return ApiErrorResponse::error(
                422,
                'STUDY_PROGRAM_NOT_AVAILABLE',
                'Program studi yang dipilih tidak aktif.',
            );
        }

        $app->update(['study_program_id' => $validated['study_program_id']]);

        return ApiErrorResponse::success([
            'id' => $app->id,
            'registration_number' => $app->registration_number,
            'study_program_id' => $app->study_program_id,
            'status' => $app->status,
        ], 'Pengajuan berhasil diperbarui.');
    }

    /**
     * POST /api/v1/applications/{id}/submit — mengirim pendaftaran untuk diperiksa.
     */
    public function submit(Request $request, int $id): JsonResponse
    {
        $user = $request->user();
        $profile = $user->applicantProfile;

        $app = Application::query()
            ->where('id', $id)
            ->where('applicant_id', $profile?->id)
            ->first();

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        $expectedStatus = $request->input('expected_status');
        if ($expectedStatus !== 'draft' || $app->status !== 'draft') {
            return ApiErrorResponse::error(
                409,
                'STATE_CONFLICT',
                'Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.',
            );
        }

        if (! $profile || ! $profile->full_name || ! $profile->nik || ! $profile->phone || ! $profile->address) {
            return ApiErrorResponse::error(
                422,
                'APPLICATION_INCOMPLETE',
                'Profil calon mahasiswa belum lengkap.',
            );
        }

        if ($app->documents()->count() === 0) {
            return ApiErrorResponse::error(
                422,
                'APPLICATION_INCOMPLETE',
                'Dokumen persyaratan wajib diunggah sebelum pengajuan dikirim.',
            );
        }

        DB::transaction(function () use ($app, $user) {
            $app->status = 'submitted';
            $app->submitted_at = now();
            $app->save();

            $app->statusHistories()->create([
                'from_status' => 'draft',
                'to_status' => 'submitted',
                'note' => 'Pendaftaran berhasil dikirim untuk pemeriksaan.',
                'changed_by_user_id' => $user->id,
            ]);
        });

        return ApiErrorResponse::success([
            'id' => $app->id,
            'registration_number' => $app->registration_number,
            'status' => $app->status,
            'submitted_at' => $app->submitted_at->toIso8601String(),
        ], 'Pendaftaran berhasil dikirim untuk pemeriksaan.');
    }

    /**
     * POST /api/v1/applications/{id}/resubmit — mengirim perbaikan setelah revisi.
     */
    public function resubmit(Request $request, int $id): JsonResponse
    {
        $user = $request->user();
        $profile = $user->applicantProfile;

        $app = Application::query()
            ->where('id', $id)
            ->where('applicant_id', $profile?->id)
            ->first();

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        $expectedStatus = $request->input('expected_status');
        if ($expectedStatus !== 'revision' || $app->status !== 'revision') {
            return ApiErrorResponse::error(
                409,
                'STATE_CONFLICT',
                'Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.',
            );
        }

        DB::transaction(function () use ($app, $user) {
            $app->status = 'review';
            $app->save();

            $app->statusHistories()->create([
                'from_status' => 'revision',
                'to_status' => 'review',
                'note' => 'Perbaikan berhasil dikirim untuk diperiksa kembali.',
                'changed_by_user_id' => $user->id,
            ]);
        });

        return ApiErrorResponse::success([
            'id' => $app->id,
            'status' => $app->status,
            'submitted_at' => $app->submitted_at?->toIso8601String(),
        ], 'Perbaikan berhasil dikirim untuk diperiksa kembali.');
    }
}
