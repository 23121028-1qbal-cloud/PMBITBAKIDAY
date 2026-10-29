<?php

namespace Tests\Feature;

use App\Models\AdmissionWave;
use App\Models\StudyProgram;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ReferenceDataTest extends TestCase
{
    use RefreshDatabase;

    public function test_reference_endpoints_require_login(): void
    {
        $this->getJson('/api/v1/admission-waves')
            ->assertStatus(401)
            ->assertJsonPath('error.code', 'UNAUTHENTICATED');

        $this->getJson('/api/v1/study-programs')
            ->assertStatus(401)
            ->assertJsonPath('error.code', 'UNAUTHENTICATED');
    }

    public function test_active_and_open_waves_are_listed(): void
    {
        AdmissionWave::factory()->create(['name' => 'Gelombang 1']);
        AdmissionWave::factory()->closed()->create(['name' => 'Gelombang Lama']);
        AdmissionWave::factory()->inactive()->create(['name' => 'Gelombang Nonaktif']);

        $response = $this->actingAs(User::factory()->create(), 'sanctum')
            ->getJson('/api/v1/admission-waves');

        $response->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonPath('message', 'Data gelombang berhasil diambil.')
            ->assertJsonCount(1, 'data');

        $wave = $response->json('data.0');
        $this->assertSame('Gelombang 1', $wave['name']);
        $this->assertArrayHasKey('start_date', $wave);
        $this->assertArrayHasKey('end_date', $wave);
        $this->assertTrue($wave['is_active']);
        // Contract §3.4: ISO 8601 with the system timezone offset.
        $this->assertMatchesRegularExpression('/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}[+-]\d{2}:\d{2}$/', $wave['start_date']);
    }

    public function test_inactive_study_programs_are_hidden(): void
    {
        StudyProgram::factory()->create(['code' => 'IF', 'name' => 'Informatika']);
        StudyProgram::factory()->inactive()->create(['code' => 'XA', 'name' => 'Program Nonaktif']);

        $this->actingAs(User::factory()->create(), 'sanctum')
            ->getJson('/api/v1/study-programs')
            ->assertStatus(200)
            ->assertJsonPath('message', 'Data program studi berhasil diambil.')
            ->assertJsonCount(1, 'data')
            ->assertJsonPath('data.0.code', 'IF')
            ->assertJsonPath('data.0.name', 'Informatika');
    }
}
