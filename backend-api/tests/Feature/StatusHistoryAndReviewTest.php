<?php

namespace Tests\Feature;

use App\Models\ApplicantProfile;
use App\Models\Application;
use App\Models\ApplicationStatusHistory;
use App\Models\Role;
use App\Models\User;
use Database\Seeders\RoleSeeder;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class StatusHistoryAndReviewTest extends TestCase
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

    private function createStaffUser(): User
    {
        $role = Role::query()->where('name', 'panitia')->first();
        return User::factory()->create(['role_id' => $role->id]);
    }

    public function test_status_history_is_viewable_by_owner_and_staff(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create(['applicant_id' => $profile->id]);

        ApplicationStatusHistory::factory()->create([
            'application_id' => $app->id,
            'from_status' => null,
            'to_status' => 'draft',
            'note' => 'Draft pendaftaran dibuat.',
        ]);

        $this->actingAs($user, 'sanctum')
            ->getJson("/api/v1/applications/{$app->id}/status-history")
            ->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonCount(1, 'data')
            ->assertJsonPath('data.0.to_status', 'draft');

        $staff = $this->createStaffUser();
        $this->actingAs($staff, 'sanctum')
            ->getJson("/api/v1/applications/{$app->id}/status-history")
            ->assertStatus(200)
            ->assertJsonCount(1, 'data');
    }

    public function test_applicant_cannot_access_committee_review_endpoints(): void
    {
        $applicant = $this->createApplicantUser();
        $app = Application::factory()->submitted()->create();

        $this->actingAs($applicant, 'sanctum')
            ->postJson("/api/v1/admin/applications/{$app->id}/review", [
                'expected_status' => 'submitted',
            ])
            ->assertStatus(403)
            ->assertJsonPath('error.code', 'FORBIDDEN');

        $this->actingAs($applicant, 'sanctum')
            ->getJson('/api/v1/admin/applications')
            ->assertStatus(403)
            ->assertJsonPath('error.code', 'FORBIDDEN');
    }

    public function test_committee_can_start_review_of_submitted_application(): void
    {
        $staff = $this->createStaffUser();
        $app = Application::factory()->submitted()->create();

        // Konflik jika expected_status tidak sesuai
        $this->actingAs($staff, 'sanctum')
            ->postJson("/api/v1/admin/applications/{$app->id}/review", [
                'expected_status' => 'review',
            ])
            ->assertStatus(409)
            ->assertJsonPath('error.code', 'STATE_CONFLICT');

        $response = $this->actingAs($staff, 'sanctum')
            ->postJson("/api/v1/admin/applications/{$app->id}/review", [
                'expected_status' => 'submitted',
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonPath('data.status', 'review');

        $this->assertDatabaseHas('applications', [
            'id' => $app->id,
            'status' => 'review',
        ]);

        $this->assertDatabaseHas('application_status_histories', [
            'application_id' => $app->id,
            'from_status' => 'submitted',
            'to_status' => 'review',
            'changed_by_user_id' => $staff->id,
        ]);
    }

    public function test_committee_can_request_revision(): void
    {
        $staff = $this->createStaffUser();
        $app = Application::factory()->review()->create();

        $response = $this->actingAs($staff, 'sanctum')
            ->postJson("/api/v1/admin/applications/{$app->id}/request-revision", [
                'expected_status' => 'review',
                'note' => 'Mohon unggah ulang ijazah dengan scan yang lebih jelas.',
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'revision')
            ->assertJsonPath('data.note', 'Mohon unggah ulang ijazah dengan scan yang lebih jelas.');

        $this->assertDatabaseHas('applications', [
            'id' => $app->id,
            'status' => 'revision',
        ]);

        $this->assertDatabaseHas('application_status_histories', [
            'application_id' => $app->id,
            'from_status' => 'review',
            'to_status' => 'revision',
            'note' => 'Mohon unggah ulang ijazah dengan scan yang lebih jelas.',
            'changed_by_user_id' => $staff->id,
        ]);
    }

    public function test_committee_can_record_acceptance_or_rejection_decision(): void
    {
        $staff = $this->createStaffUser();
        $app = Application::factory()->review()->create();

        // Keputusan diterima
        $response = $this->actingAs($staff, 'sanctum')
            ->postJson("/api/v1/admin/applications/{$app->id}/decision", [
                'expected_status' => 'review',
                'decision' => 'accepted',
                'note' => 'Persyaratan terpenuhi dan dinyatakan diterima.',
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonPath('data.status', 'accepted')
            ->assertJsonPath('message', 'Keputusan pendaftaran berhasil disimpan.');

        $this->assertDatabaseHas('applications', [
            'id' => $app->id,
            'status' => 'accepted',
        ]);

        $this->assertDatabaseHas('application_status_histories', [
            'application_id' => $app->id,
            'from_status' => 'review',
            'to_status' => 'accepted',
            'changed_by_user_id' => $staff->id,
        ]);
    }

    public function test_admin_application_list_and_detail(): void
    {
        $staff = $this->createStaffUser();
        $app = Application::factory()->submitted()->create();

        $this->actingAs($staff, 'sanctum')
            ->getJson('/api/v1/admin/applications')
            ->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonCount(1, 'data');

        $this->actingAs($staff, 'sanctum')
            ->getJson("/api/v1/admin/applications/{$app->id}")
            ->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonPath('data.id', $app->id)
            ->assertJsonPath('data.status', 'submitted');
    }
}
