<?php

namespace Tests\Feature;

use App\Models\AdmissionWave;
use App\Models\ApplicantProfile;
use App\Models\Application;
use App\Models\ApplicationDocument;
use App\Models\Role;
use App\Models\StudyProgram;
use App\Models\User;
use Database\Seeders\RoleSeeder;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ApplicationTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();
        $this->seed(RoleSeeder::class);
    }

    private function createApplicantUser(): User
    {
        $role = Role::query()->where('name', 'calon_mahasiswa')->first();
        return User::factory()->create(['role_id' => $role->id]);
    }

    public function test_applications_require_authentication(): void
    {
        $this->postJson('/api/v1/applications', [])
            ->assertStatus(401)
            ->assertJsonPath('error.code', 'UNAUTHENTICATED');

        $this->getJson('/api/v1/applications')
            ->assertStatus(401)
            ->assertJsonPath('error.code', 'UNAUTHENTICATED');
    }

    public function test_store_requires_complete_applicant_profile(): void
    {
        $user = $this->createApplicantUser();
        $wave = AdmissionWave::factory()->create();
        $program = StudyProgram::factory()->create();

        $this->actingAs($user, 'sanctum')
            ->postJson('/api/v1/applications', [
                'wave_id' => $wave->id,
                'study_program_id' => $program->id,
            ])
            ->assertStatus(422)
            ->assertJsonPath('error.code', 'APPLICATION_INCOMPLETE');
    }

    public function test_store_creates_draft_application_with_status_history(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $wave = AdmissionWave::factory()->create();
        $program = StudyProgram::factory()->create();

        $response = $this->actingAs($user, 'sanctum')
            ->postJson('/api/v1/applications', [
                'wave_id' => $wave->id,
                'study_program_id' => $program->id,
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('success', true)
            ->assertJsonPath('message', 'Draft pendaftaran berhasil dibuat.')
            ->assertJsonPath('data.status', 'draft')
            ->assertJsonPath('data.wave_id', $wave->id)
            ->assertJsonPath('data.study_program_id', $program->id);

        $appId = $response->json('data.id');

        $this->assertDatabaseHas('applications', [
            'id' => $appId,
            'applicant_id' => $profile->id,
            'wave_id' => $wave->id,
            'status' => 'draft',
        ]);

        $this->assertDatabaseHas('application_status_histories', [
            'application_id' => $appId,
            'from_status' => null,
            'to_status' => 'draft',
            'changed_by_user_id' => $user->id,
        ]);
    }

    public function test_store_rejects_duplicate_application_in_same_wave(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $wave = AdmissionWave::factory()->create();
        $program = StudyProgram::factory()->create();

        Application::factory()->create([
            'applicant_id' => $profile->id,
            'wave_id' => $wave->id,
            'study_program_id' => $program->id,
        ]);

        $this->actingAs($user, 'sanctum')
            ->postJson('/api/v1/applications', [
                'wave_id' => $wave->id,
                'study_program_id' => $program->id,
            ])
            ->assertStatus(409)
            ->assertJsonPath('error.code', 'APPLICATION_ALREADY_EXISTS');
    }

    public function test_index_returns_paginated_applications_for_authenticated_applicant(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);

        $otherUser = $this->createApplicantUser();
        $otherProfile = ApplicantProfile::factory()->create(['user_id' => $otherUser->id]);

        Application::factory()->create(['applicant_id' => $profile->id]);
        Application::factory()->create(['applicant_id' => $otherProfile->id]);

        $response = $this->actingAs($user, 'sanctum')
            ->getJson('/api/v1/applications');

        $response->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonCount(1, 'data')
            ->assertJsonStructure([
                'success',
                'message',
                'data' => [
                    '*' => ['id', 'registration_number', 'wave', 'study_program', 'status', 'submitted_at'],
                ],
                'meta' => ['current_page', 'per_page', 'total', 'last_page'],
            ]);
    }

    public function test_show_allows_owner_and_forbids_other_applicants(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create(['applicant_id' => $profile->id]);

        $otherUser = $this->createApplicantUser();
        ApplicantProfile::factory()->create(['user_id' => $otherUser->id]);

        $this->actingAs($user, 'sanctum')
            ->getJson('/api/v1/applications/' . $app->id)
            ->assertStatus(200)
            ->assertJsonPath('data.id', $app->id);

        $this->actingAs($otherUser, 'sanctum')
            ->getJson('/api/v1/applications/' . $app->id)
            ->assertStatus(404)
            ->assertJsonPath('error.code', 'RESOURCE_NOT_FOUND');
    }

    public function test_update_modifies_draft_study_program(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $oldProgram = StudyProgram::factory()->create(['code' => 'IF']);
        $newProgram = StudyProgram::factory()->create(['code' => 'SI']);
        $app = Application::factory()->create([
            'applicant_id' => $profile->id,
            'study_program_id' => $oldProgram->id,
            'status' => 'draft',
        ]);

        $this->actingAs($user, 'sanctum')
            ->putJson('/api/v1/applications/' . $app->id, [
                'study_program_id' => $newProgram->id,
            ])
            ->assertStatus(200)
            ->assertJsonPath('data.study_program_id', $newProgram->id);

        $this->assertDatabaseHas('applications', [
            'id' => $app->id,
            'study_program_id' => $newProgram->id,
        ]);
    }

    public function test_submit_requires_expected_status_draft_and_complete_documents(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create([
            'applicant_id' => $profile->id,
            'status' => 'draft',
        ]);

        // Gagal jika expected_status tidak sesuai
        $this->actingAs($user, 'sanctum')
            ->postJson('/api/v1/applications/' . $app->id . '/submit', [
                'expected_status' => 'review',
            ])
            ->assertStatus(409)
            ->assertJsonPath('error.code', 'STATE_CONFLICT');

        // Gagal jika belum ada dokumen yang diunggah
        $this->actingAs($user, 'sanctum')
            ->postJson('/api/v1/applications/' . $app->id . '/submit', [
                'expected_status' => 'draft',
            ])
            ->assertStatus(422)
            ->assertJsonPath('error.code', 'APPLICATION_INCOMPLETE');

        // Unggah dokumen, lalu submit harus berhasil
        ApplicationDocument::factory()->create(['application_id' => $app->id]);

        $response = $this->actingAs($user, 'sanctum')
            ->postJson('/api/v1/applications/' . $app->id . '/submit', [
                'expected_status' => 'draft',
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonPath('data.status', 'submitted')
            ->assertJsonPath('message', 'Pendaftaran berhasil dikirim untuk pemeriksaan.');

        $this->assertDatabaseHas('applications', [
            'id' => $app->id,
            'status' => 'submitted',
        ]);

        $this->assertDatabaseHas('application_status_histories', [
            'application_id' => $app->id,
            'from_status' => 'draft',
            'to_status' => 'submitted',
        ]);
    }

    public function test_resubmit_transitions_revision_to_review(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->revision()->create([
            'applicant_id' => $profile->id,
        ]);

        // State conflict jika precondition tidak cocok
        $this->actingAs($user, 'sanctum')
            ->postJson('/api/v1/applications/' . $app->id . '/resubmit', [
                'expected_status' => 'submitted',
            ])
            ->assertStatus(409)
            ->assertJsonPath('error.code', 'STATE_CONFLICT');

        $response = $this->actingAs($user, 'sanctum')
            ->postJson('/api/v1/applications/' . $app->id . '/resubmit', [
                'expected_status' => 'revision',
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'review');

        $this->assertDatabaseHas('applications', [
            'id' => $app->id,
            'status' => 'review',
        ]);

        $this->assertDatabaseHas('application_status_histories', [
            'application_id' => $app->id,
            'from_status' => 'revision',
            'to_status' => 'review',
        ]);
    }
}
