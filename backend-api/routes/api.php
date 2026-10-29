<?php

use App\Http\Controllers\Api\AdminApplicationController;
use App\Http\Controllers\Api\ApplicationController;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\DocumentController;
use App\Http\Controllers\Api\ProfileController;
use App\Http\Controllers\Api\ReferenceController;
use App\Http\Controllers\Api\StatusHistoryController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes — prefix /api/v1 (API Contract, Tahap 4)
|--------------------------------------------------------------------------
|
| The contract defines two authentication contexts that must never be mixed
| inside a single client session:
|
|   - Android:   POST /api/v1/auth/login issues a Sanctum bearer token.
|   - Admin web: POST /api/v1/auth/session/login authenticates the first-party
|                browser session. It requires the CSRF bootstrap endpoint
|                GET /sanctum/csrf-cookie, which lives outside /api/v1.
|
| Resource endpoints below accept whichever context is valid for the caller;
| they never accept both at once.
|
*/

// Public authentication endpoints.
Route::post('/auth/register', [AuthController::class, 'register'])
    ->middleware('throttle:10,1');

Route::post('/auth/login', [AuthController::class, 'login'])
    ->middleware('throttle:10,1');

// Browser session flow: session cookies plus CSRF protection.
Route::post('/auth/session/login', [AuthController::class, 'sessionLogin'])
    ->middleware([\Illuminate\Session\Middleware\StartSession::class])
    ->middleware('throttle:10,1');

// Authenticated endpoints — one of the two Sanctum contexts must be present.
Route::middleware('auth:sanctum')->group(function () {
    Route::get('/auth/me', [AuthController::class, 'me']);
    Route::post('/auth/logout', [AuthController::class, 'logout']);
    Route::post('/auth/session/logout', [AuthController::class, 'sessionLogout']);

    // Reference data used by both clients while composing an application.
    Route::get('/admission-waves', [ReferenceController::class, 'waves']);
    Route::get('/study-programs', [ReferenceController::class, 'studyPrograms']);

    // Applicant profile is always scoped to the authenticated account.
    Route::get('/applicant/profile', [ProfileController::class, 'show']);
    Route::put('/applicant/profile', [ProfileController::class, 'update']);

    // Pengajuan pendaftaran.
    Route::post('/applications', [ApplicationController::class, 'store']);
    Route::get('/applications', [ApplicationController::class, 'index']);
    Route::get('/applications/{id}', [ApplicationController::class, 'show']);
    Route::put('/applications/{id}', [ApplicationController::class, 'update']);
    Route::post('/applications/{id}/submit', [ApplicationController::class, 'submit']);
    Route::post('/applications/{id}/resubmit', [ApplicationController::class, 'resubmit']);

    // Dokumen pendaftaran.
    Route::post('/applications/{id}/documents', [DocumentController::class, 'store']);
    Route::get('/applications/{id}/documents', [DocumentController::class, 'index']);
    Route::get('/applications/{id}/documents/{documentId}/download', [DocumentController::class, 'download']);
    Route::delete('/applications/{id}/documents/{documentId}', [DocumentController::class, 'destroy']);

    // Riwayat status pendaftaran.
    Route::get('/applications/{id}/status-history', [StatusHistoryController::class, 'index']);

    // Area Admin / Panitia / Pimpinan.
    Route::middleware('role:admin,panitia,pimpinan')->prefix('admin')->group(function () {
        Route::get('/applications', [AdminApplicationController::class, 'index']);
        Route::get('/applications/{id}', [AdminApplicationController::class, 'show']);
    });

    Route::middleware('role:admin,panitia')->prefix('admin')->group(function () {
        Route::post('/applications/{id}/review', [StatusHistoryController::class, 'review']);
        Route::post('/applications/{id}/request-revision', [StatusHistoryController::class, 'requestRevision']);
        Route::post('/applications/{id}/decision', [StatusHistoryController::class, 'decide']);
    });
});
