<?php

namespace App\Http\Controllers\Api;

use App\Exceptions\AccountInactiveException;
use App\Http\Controllers\Controller;
use App\Models\Role;
use App\Models\User;
use App\Support\ApiErrorResponse;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    /**
     * POST /api/v1/auth/register — public registration for applicants.
     */
    public function register(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'email' => ['required', 'email', 'max:255', 'unique:users,email'],
            'password' => ['required', 'string', 'min:8', 'confirmed'],
        ]);

        $user = User::create([
            'email' => $validated['email'],
            'password' => Hash::make($validated['password']),
            'role_id' => $this->applicantRoleId(),
            'is_active' => true,
        ]);

        return ApiErrorResponse::success([
            'id' => $user->id,
            'email' => $user->email,
            'role' => 'calon_mahasiswa',
        ], 'Akun berhasil dibuat.', 201);
    }

    /**
     * POST /api/v1/auth/login — Android token flow.
     *
     * Returns a Sanctum personal access token. This endpoint never creates a
     * browser session and never sets auth cookies.
     */
    public function login(Request $request): JsonResponse
    {
        $user = $this->authenticate($request);

        $token = $user->createToken('android')->plainTextToken;

        return ApiErrorResponse::success([
            'user' => $this->userPayload($user),
            'access_token' => $token,
            'token_type' => 'Bearer',
        ], 'Login berhasil.');
    }

    /**
     * POST /api/v1/auth/session/login — admin browser session flow.
     *
     * Authenticates the first-party browser session. The session cookie is
     * set by the framework; no bearer token is issued or returned here.
     */
    public function sessionLogin(Request $request): JsonResponse
    {
        $user = $this->authenticate($request);

        // Put the user in the web session (Sanctum's stateful flow reads the
        // session guard), rotate the session id against fixation, and rotate
        // the CSRF token so a pre-login token cannot be replayed after login.
        auth('web')->login($user);
        $request->session()->regenerate();
        $request->session()->regenerateToken();

        return ApiErrorResponse::success([
            'user' => $this->userPayload($user),
        ], 'Login berhasil.');
    }

    /**
     * POST /api/v1/auth/logout — revokes the current Android bearer token.
     */
    public function logout(Request $request): JsonResponse
    {
        $request->user()?->currentAccessToken()?->delete();

        return ApiErrorResponse::success(null, 'Logout berhasil.');
    }

    /**
     * POST /api/v1/auth/session/logout — ends the admin browser session.
     */
    public function sessionLogout(Request $request): JsonResponse
    {
        auth('web')->logout();

        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return ApiErrorResponse::success(null, 'Logout berhasil.');
    }

    /**
     * GET /api/v1/auth/me — current authenticated user for either flow.
     */
    public function me(Request $request): JsonResponse
    {
        $user = $request->user();

        if (! $user) {
            return ApiErrorResponse::error(401, 'UNAUTHENTICATED', 'Silakan login terlebih dahulu.');
        }

        return ApiErrorResponse::success($this->userPayload($user), 'Data akun berhasil diambil.');
    }

    /**
     * Shared credential check for both authentication flows.
     *
     * A single generic message is returned for "unknown email" and "wrong
     * password" so responses cannot be used to enumerate registered accounts.
     */
    private function authenticate(Request $request): User
    {
        $credentials = $request->validate([
            'email' => ['required', 'email'],
            'password' => ['required', 'string'],
        ]);

        $user = User::query()->where('email', $credentials['email'])->first();

        if (! $user || ! Hash::check($credentials['password'], $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['Email atau password tidak valid.'],
            ])->status(401);
        }

        if (! $user->is_active) {
            throw new AccountInactiveException();
        }

        return $user;
    }

    private function userPayload(User $user): array
    {
        return [
            'id' => $user->id,
            'email' => $user->email,
            'role' => $user->role?->name ?? 'calon_mahasiswa',
        ];
    }

    private function applicantRoleId(): int
    {
        $role = Role::query()->where('name', 'calon_mahasiswa')->first();

        if (! $role) {
            abort(500, 'Data peran calon_mahasiswa belum tersedia. Jalankan RoleSeeder terlebih dahulu.');
        }

        return $role->id;
    }
}
