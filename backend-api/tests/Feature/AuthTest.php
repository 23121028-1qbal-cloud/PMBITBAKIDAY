<?php

namespace Tests\Feature;

use App\Models\User;
use Database\Seeders\RoleSeeder;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AuthTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();

        // Registration resolves the applicant role created by RoleSeeder.
        $this->seed(RoleSeeder::class);
    }

    public function test_register_creates_applicant_account(): void
    {
        $response = $this->postJson('/api/v1/auth/register', [
            'email' => 'mahasiswa@example.com',
            'password' => 'ContohPasswordAman123',
            'password_confirmation' => 'ContohPasswordAman123',
        ]);

        $response->assertStatus(201)
            ->assertJsonPath('success', true)
            ->assertJsonPath('data.role', 'calon_mahasiswa');

        $this->assertDatabaseHas('users', ['email' => 'mahasiswa@example.com']);
    }

    public function test_register_rejects_duplicate_email_with_documented_envelope(): void
    {
        User::factory()->create(['email' => 'mahasiswa@example.com']);

        $response = $this->postJson('/api/v1/auth/register', [
            'email' => 'mahasiswa@example.com',
            'password' => 'ContohPasswordAman123',
            'password_confirmation' => 'ContohPasswordAman123',
        ]);

        $response->assertStatus(422)
            ->assertJsonPath('success', false)
            ->assertJsonPath('error.code', 'VALIDATION_ERROR');
    }

    public function test_android_login_returns_bearer_token(): void
    {
        User::factory()->create([
            'email' => 'mahasiswa@example.com',
            'password' => 'ContohPasswordAman123',
        ]);

        $response = $this->postJson('/api/v1/auth/login', [
            'email' => 'mahasiswa@example.com',
            'password' => 'ContohPasswordAman123',
        ]);

        $response->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonPath('data.token_type', 'Bearer');

        $this->assertNotEmpty($response->json('data.access_token'));
    }

    public function test_login_failure_uses_single_generic_message(): void
    {
        User::factory()->create([
            'email' => 'mahasiswa@example.com',
            'password' => 'ContohPasswordAman123',
        ]);

        $unknownResponse = $this->postJson('/api/v1/auth/login', [
            'email' => 'belum-terdaftar@example.com',
            'password' => 'ApapunPassword123',
        ]);

        $wrongPasswordResponse = $this->postJson('/api/v1/auth/login', [
            'email' => 'mahasiswa@example.com',
            'password' => 'PasswordSalah123',
        ]);

        $unknownResponse->assertStatus(401)->assertJsonPath('error.code', 'INVALID_CREDENTIALS');
        $wrongPasswordResponse->assertStatus(401)->assertJsonPath('error.code', 'INVALID_CREDENTIALS');

        $unknownEmail = $unknownResponse->json();
        $wrongPassword = $wrongPasswordResponse->json();

        $this->assertSame($unknownEmail['error']['code'], $wrongPassword['error']['code']);
        $this->assertSame($unknownEmail['message'], $wrongPassword['message']);

        // API Error Handling §6.1 / ERR-01: never a field-level 422 that
        // would confirm whether the submitted email is registered.
        $this->assertSame('INVALID_CREDENTIALS', $unknownEmail['error']['code']);
        $this->assertSame('Email atau password tidak valid.', $unknownEmail['message']);
    }

    public function test_me_requires_authentication_and_returns_envelope(): void
    {
        $this->getJson('/api/v1/auth/me')
            ->assertStatus(401)
            ->assertJsonPath('success', false)
            ->assertJsonPath('error.code', 'UNAUTHENTICATED');
    }

    public function test_me_works_with_bearer_token(): void
    {
        $user = User::factory()->create(['email' => 'mahasiswa@example.com']);

        $this->actingAs($user, 'sanctum')
            ->getJson('/api/v1/auth/me')
            ->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonPath('data.email', 'mahasiswa@example.com');
    }

    public function test_session_flow_returns_no_bearer_token(): void
    {
        User::factory()->create([
            'email' => 'admin@example.com',
            'password' => 'ContohPasswordAman123',
        ]);

        $response = $this->withSession([])
            ->postJson('/api/v1/auth/session/login', [
                'email' => 'admin@example.com',
                'password' => 'ContohPasswordAman123',
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('success', true);

        $this->assertArrayNotHasKey('access_token', $response->json('data'));
    }

    public function test_deactivated_account_is_rejected(): void
    {
        User::factory()->inactive()->create([
            'email' => 'mahasiswa@example.com',
            'password' => 'ContohPasswordAman123',
        ]);

        $this->postJson('/api/v1/auth/login', [
            'email' => 'mahasiswa@example.com',
            'password' => 'ContohPasswordAman123',
        ])
            ->assertStatus(403)
            ->assertJsonPath('error.code', 'ACCOUNT_INACTIVE');
    }

    public function test_validation_error_uses_documented_envelope(): void
    {
        $this->postJson('/api/v1/auth/register', [
            'email' => 'bukan-email',
            'password' => 'pendek',
            'password_confirmation' => 'berbeda',
        ])
            ->assertStatus(422)
            ->assertJsonPath('success', false)
            ->assertJsonPath('error.code', 'VALIDATION_ERROR')
 ->assertJsonStructure(['error' => ['code', 'details']]);
    }
}
