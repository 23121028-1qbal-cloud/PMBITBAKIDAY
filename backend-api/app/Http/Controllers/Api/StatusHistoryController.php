<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Application;
use App\Models\ApplicationStatusHistory;
use App\Support\ApiErrorResponse;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\Rule;

class StatusHistoryController extends Controller
{
    /**
     * GET /api/v1/applications/{id}/status-history — melihat riwayat status pengajuan.
     */
    public function index(Request $request, int $applicationId): JsonResponse
    {
        $user = $request->user();
        $app = Application::query()->find($applicationId);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        if (! $user->hasRole(['admin', 'panitia', 'pimpinan'])) {
            $profile = $user->applicantProfile;
            if (! $profile || $app->applicant_id !== $profile->id) {
                return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
            }
        }

        $histories = ApplicationStatusHistory::query()
            ->where('application_id', $applicationId)
            ->orderBy('id', 'asc')
            ->get();

        $data = $histories->map(fn (ApplicationStatusHistory $history) => [
            'from_status' => $history->from_status,
            'to_status' => $history->to_status,
            'note' => $history->note,
            'changed_at' => $history->created_at?->toIso8601String(),
        ])->values()->all();

        return ApiErrorResponse::success($data, 'Riwayat status berhasil diambil.');
    }

    /**
     * POST /api/v1/admin/applications/{id}/review — memulai pemeriksaan aplikasi (submitted -> review).
     */
    public function review(Request $request, int $id): JsonResponse
    {
        $app = Application::query()->find($id);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        $validated = $request->validate([
            'expected_status' => ['required', 'string'],
        ]);

        if ($app->status !== 'submitted' || $validated['expected_status'] !== 'submitted') {
            return ApiErrorResponse::error(
                409,
                'STATE_CONFLICT',
                'Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.',
            );
        }

        DB::transaction(function () use ($app, $request) {
            $app->status = 'review';
            $app->save();

            $app->statusHistories()->create([
                'from_status' => 'submitted',
                'to_status' => 'review',
                'note' => 'Pemeriksaan dimulai oleh panitia.',
                'changed_by_user_id' => $request->user()->id,
            ]);
        });

        return ApiErrorResponse::success([
            'id' => $app->id,
            'status' => 'review',
        ], 'Pemeriksaan pendaftaran dimulai.');
    }

    /**
     * POST /api/v1/admin/applications/{id}/request-revision — meminta perbaikan berkas (review -> revision).
     */
    public function requestRevision(Request $request, int $id): JsonResponse
    {
        $app = Application::query()->find($id);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        $validated = $request->validate([
            'expected_status' => ['required', 'string'],
            'note' => ['required', 'string', 'min:3', 'max:2000'],
        ]);

        if ($app->status !== 'review' || $validated['expected_status'] !== 'review') {
            return ApiErrorResponse::error(
                409,
                'STATE_CONFLICT',
                'Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.',
            );
        }

        DB::transaction(function () use ($app, $validated, $request) {
            $app->status = 'revision';
            $app->save();

            $app->statusHistories()->create([
                'from_status' => 'review',
                'to_status' => 'revision',
                'note' => $validated['note'],
                'changed_by_user_id' => $request->user()->id,
            ]);
        });

        return ApiErrorResponse::success([
            'id' => $app->id,
            'status' => 'revision',
            'note' => $validated['note'],
        ], 'Permintaan revisi berhasil dicatat.');
    }

    /**
     * POST /api/v1/admin/applications/{id}/decision — menetapkan keputusan akhir (review -> accepted/rejected).
     */
    public function decide(Request $request, int $id): JsonResponse
    {
        $app = Application::query()->find($id);

        if (! $app) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data pengajuan tidak ditemukan.');
        }

        $validated = $request->validate([
            'expected_status' => ['required', 'string'],
            'decision' => ['required', 'string', Rule::in(['accepted', 'rejected'])],
            'note' => ['required', 'string', 'min:3', 'max:2000'],
        ]);

        if ($app->status !== 'review' || $validated['expected_status'] !== 'review') {
            return ApiErrorResponse::error(
                409,
                'STATE_CONFLICT',
                'Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.',
            );
        }

        $now = now();

        DB::transaction(function () use ($app, $validated, $request, $now) {
            $app->status = $validated['decision'];
            $app->save();

            $app->statusHistories()->create([
                'from_status' => 'review',
                'to_status' => $validated['decision'],
                'note' => $validated['note'],
                'changed_by_user_id' => $request->user()->id,
                'created_at' => $now,
            ]);
        });

        return ApiErrorResponse::success([
            'id' => $app->id,
            'status' => $validated['decision'],
            'decided_at' => $now->toIso8601String(),
        ], 'Keputusan pendaftaran berhasil disimpan.');
    }
}
