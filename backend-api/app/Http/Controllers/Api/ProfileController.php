<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\ApplicantProfile;
use App\Support\ApiErrorResponse;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class ProfileController extends Controller
{
    /**
     * GET /api/v1/applicant/profile — the profile belonging to the caller.
     *
     * The profile is always resolved from the authenticated account (API
     * Contract §6.3): a client-supplied ID is never used to decide whose
     * profile is returned (BR-07).
     */
    public function show(Request $request): JsonResponse
    {
        $profile = $this->profileFor($request);

        if (! $profile) {
            return ApiErrorResponse::error(404, 'RESOURCE_NOT_FOUND', 'Data yang diminta tidak ditemukan.');
        }

        return ApiErrorResponse::success($this->payload($profile), 'Profil berhasil diambil.');
    }

    /**
     * PUT /api/v1/applicant/profile — create or update the caller's profile.
     *
     * The contract describes this endpoint as create-or-update, so a single
     * updateOrCreate on the unique user_id keeps one profile per account.
     * Global NIK uniqueness (OD-02) is deliberately not enforced here because
     * that decision is still open.
     */
    public function update(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'full_name' => ['required', 'string', 'max:150'],
            'nik' => ['required', 'string', 'digits:16'],
            'birth_place' => ['required', 'string', 'max:100'],
            'birth_date' => ['required', 'date', 'before_or_equal:today'],
            'gender' => ['required', 'string', 'in:L,P'],
            'phone' => ['required', 'string', 'max:20', 'regex:/^[0-9+\-\s()]+$/'],
            'address' => ['required', 'string', 'max:2000'],
        ]);

        $profile = ApplicantProfile::query()->updateOrCreate(
            ['user_id' => $request->user()->id],
            $validated,
        );

        return ApiErrorResponse::success($this->payload($profile), 'Profil berhasil disimpan.');
    }

    private function profileFor(Request $request): ?ApplicantProfile
    {
        return ApplicantProfile::query()->where('user_id', $request->user()->id)->first();
    }

    private function payload(ApplicantProfile $profile): array
    {
        return [
            'user_id' => $profile->user_id,
            'full_name' => $profile->full_name,
            'nik' => $profile->nik,
            'birth_place' => $profile->birth_place,
            'birth_date' => $profile->birth_date->toDateString(),
            'gender' => $profile->gender,
            'phone' => $profile->phone,
            'address' => $profile->address,
        ];
    }
}
