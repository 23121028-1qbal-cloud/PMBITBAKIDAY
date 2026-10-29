<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\AdmissionWave;
use App\Models\StudyProgram;
use App\Support\ApiErrorResponse;
use Illuminate\Http\JsonResponse;

class ReferenceController extends Controller
{
    /**
     * GET /api/v1/admission-waves — active waves an applicant may register in.
     *
     * The public listing (API Contract §6.5) only exposes waves that are still
     * usable: inactive waves and waves that have already closed are hidden.
     * A wave that has not opened yet stays visible so applicants can plan.
     */
    public function waves(): JsonResponse
    {
        $waves = AdmissionWave::query()
            ->where('is_active', true)
            ->where('end_date', '>=', now())
            ->orderBy('start_date')
            ->orderBy('id')
            ->get()
            ->map(fn (AdmissionWave $wave) => [
                'id' => $wave->id,
                'name' => $wave->name,
                'start_date' => $wave->start_date->toIso8601String(),
                'end_date' => $wave->end_date->toIso8601String(),
                'is_active' => $wave->is_active,
            ])
            ->values()
            ->all();

        return ApiErrorResponse::success($waves, 'Data gelombang berhasil diambil.');
    }

    /**
     * GET /api/v1/study-programs — active study programs.
     *
     * Per BR-12 the list is prototype data until the institution confirms it,
     * and only active rows may be offered for selection (API Contract §6.6).
     */
    public function studyPrograms(): JsonResponse
    {
        $programs = StudyProgram::query()
            ->where('is_active', true)
            ->orderBy('code')
            ->orderBy('id')
            ->get()
            ->map(fn (StudyProgram $program) => [
                'id' => $program->id,
                'code' => $program->code,
                'name' => $program->name,
            ])
            ->values()
            ->all();

        return ApiErrorResponse::success($programs, 'Data program studi berhasil diambil.');
    }
}
