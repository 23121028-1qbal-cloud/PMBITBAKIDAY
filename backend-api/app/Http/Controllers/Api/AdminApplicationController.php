<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Application;
use App\Support\ApiErrorResponse;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class AdminApplicationController extends Controller
{
    /**
     * GET /api/v1/admin/applications — melihat daftar pengajuan untuk admin & panitia.
     */
    public function index(Request $request): JsonResponse
    {
        $query = Application::query()
            ->with(['wave', 'studyProgram', 'applicant'])
            ->orderByDesc('created_at');

        if ($request->filled('status')) {
            $query->where('status', $request->input('status'));
        }

        if ($request->filled('wave_id')) {
            $query->where('wave_id', $request->input('wave_id'));
        }

        if ($request->filled('study_program_id')) {
            $query->where('study_program_id', $request->input('study_program_id'));
        }

        $perPage = (int) $request->input('per_page', 20);
        $perPage = min(max($perPage, 1), 100);
        $page = (int) $request->input('page', 1);
        $page = max($page, 1);

        $total = $query->count();
        $applications = $query->offset(($page - 1) * $perPage)->limit($perPage)->get();

        $data = $applications->map(fn (Application $app) => [
            'id' => $app->id,
            'registration_number' => $app->registration_number,
            'applicant' => $app->applicant ? [
                'full_name' => $app->applicant->full_name,
                'nik' => $app->applicant->nik,
            ] : null,
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
     * GET /api/v1/admin/applications/{id} — melihat detail pengajuan lengkap.
     */
    public function show(int $id): JsonResponse
    {
        $app = Application::query()
            ->with(['wave', 'studyProgram', 'applicant', 'documents', 'statusHistories'])
            ->find($id);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        return ApiErrorResponse::success([
            'id' => $app->id,
            'registration_number' => $app->registration_number,
            'status' => $app->status,
            'submitted_at' => $app->submitted_at?->toIso8601String(),
            'wave' => [
                'id' => $app->wave?->id,
                'name' => $app->wave?->name,
            ],
            'study_program' => [
                'id' => $app->studyProgram?->id,
                'name' => $app->studyProgram?->name,
            ],
            'applicant' => $app->applicant ? [
                'id' => $app->applicant->id,
                'user_id' => $app->applicant->user_id,
                'full_name' => $app->applicant->full_name,
                'nik' => $app->applicant->nik,
                'birth_place' => $app->applicant->birth_place,
                'birth_date' => $app->applicant->birth_date?->toDateString(),
                'gender' => $app->applicant->gender,
                'phone' => $app->applicant->phone,
                'address' => $app->applicant->address,
            ] : null,
            'documents' => $app->documents->map(fn ($doc) => [
                'id' => $doc->id,
                'document_type' => $doc->document_type,
                'file_name' => basename($doc->file_path),
                'file_size' => (int) $doc->file_size,
                'uploaded_at' => $doc->uploaded_at?->toIso8601String(),
            ])->values()->all(),
            'status_histories' => $app->statusHistories->map(fn ($h) => [
                'from_status' => $h->from_status,
                'to_status' => $h->to_status,
                'note' => $h->note,
                'changed_at' => $h->created_at?->toIso8601String(),
            ])->values()->all(),
        ], 'Detail pengajuan berhasil diambil.');
    }
}
