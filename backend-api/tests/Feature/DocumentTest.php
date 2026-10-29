<?php

namespace Tests\Feature;

use App\Models\ApplicantProfile;
use App\Models\Application;
use App\Models\ApplicationDocument;
use App\Models\Role;
use App\Models\User;
use Database\Seeders\RoleSeeder;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Http\UploadedFile;
use Illuminate\Support\Facades\Storage;
use Tests\TestCase;

class DocumentTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();
        $this->seed(RoleSeeder::class);
        Storage::fake('local');
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

    public function test_upload_document_stores_file_and_metadata(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create([
            'applicant_id' => $profile->id,
            'status' => 'draft',
        ]);

        $file = UploadedFile::fake()->create('ijazah.pdf', 250, 'application/pdf');

        $response = $this->actingAs($user, 'sanctum')
            ->postJson("/api/v1/applications/{$app->id}/documents", [
                'document_type' => 'diploma',
                'file' => $file,
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('success', true)
            ->assertJsonPath('data.document_type', 'diploma')
            ->assertJsonPath('data.application_id', $app->id);

        $docId = $response->json('data.id');
        $doc = ApplicationDocument::find($docId);
        $this->assertNotNull($doc);

        Storage::disk('local')->assertExists($doc->file_path);
    }

    public function test_upload_replaces_existing_document_of_same_type(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create([
            'applicant_id' => $profile->id,
            'status' => 'draft',
        ]);

        $file1 = UploadedFile::fake()->create('foto_lama.jpg', 100, 'image/jpeg');
        $this->actingAs($user, 'sanctum')
            ->postJson("/api/v1/applications/{$app->id}/documents", [
                'document_type' => 'photo',
                'file' => $file1,
            ])->assertStatus(201);

        $file2 = UploadedFile::fake()->create('foto_baru.jpg', 150, 'image/jpeg');
        $this->actingAs($user, 'sanctum')
            ->postJson("/api/v1/applications/{$app->id}/documents", [
                'document_type' => 'photo',
                'file' => $file2,
            ])->assertStatus(201);

        $this->assertSame(1, ApplicationDocument::query()->where('application_id', $app->id)->where('document_type', 'photo')->count());
    }

    public function test_upload_rejected_when_application_not_editable(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->submitted()->create([
            'applicant_id' => $profile->id,
        ]);

        $file = UploadedFile::fake()->create('ijazah.pdf', 250, 'application/pdf');

        $this->actingAs($user, 'sanctum')
            ->postJson("/api/v1/applications/{$app->id}/documents", [
                'document_type' => 'diploma',
                'file' => $file,
            ])
            ->assertStatus(422)
            ->assertJsonPath('error.code', 'APPLICATION_NOT_EDITABLE');
    }

    public function test_index_lists_documents_for_owner_and_staff(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create(['applicant_id' => $profile->id]);

        ApplicationDocument::factory()->create([
            'application_id' => $app->id,
            'document_type' => 'photo',
        ]);

        // Pemilik bisa melihat dokumen
        $this->actingAs($user, 'sanctum')
            ->getJson("/api/v1/applications/{$app->id}/documents")
            ->assertStatus(200)
            ->assertJsonPath('success', true)
            ->assertJsonCount(1, 'data');

        // Panitia juga bisa melihat
        $staff = $this->createStaffUser();
        $this->actingAs($staff, 'sanctum')
            ->getJson("/api/v1/applications/{$app->id}/documents")
            ->assertStatus(200)
            ->assertJsonCount(1, 'data');

        // Calon mahasiswa lain tidak bisa melihat
        $otherUser = $this->createApplicantUser();
        ApplicantProfile::factory()->create(['user_id' => $otherUser->id]);

        $this->actingAs($otherUser, 'sanctum')
            ->getJson("/api/v1/applications/{$app->id}/documents")
            ->assertStatus(404)
            ->assertJsonPath('error.code', 'RESOURCE_NOT_FOUND');
    }

    public function test_download_serves_private_file_to_authorized_users(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create(['applicant_id' => $profile->id]);

        $filePath = "documents/{$app->id}/doc_test.pdf";
        Storage::disk('local')->put($filePath, 'konten file dummy');

        $doc = ApplicationDocument::factory()->create([
            'application_id' => $app->id,
            'file_path' => $filePath,
            'mime_type' => 'application/pdf',
        ]);

        $response = $this->actingAs($user, 'sanctum')
            ->get("/api/v1/applications/{$app->id}/documents/{$doc->id}/download");

        $response->assertStatus(200);
        $this->assertSame('konten file dummy', $response->streamedContent());
    }

    public function test_upload_rejects_disallowed_file_format(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create([
            'applicant_id' => $profile->id,
            'status' => 'draft',
        ]);

        $file = UploadedFile::fake()->create('catatan.txt', 10, 'text/plain');

        $this->actingAs($user, 'sanctum')
            ->postJson("/api/v1/applications/{$app->id}/documents", [
                'document_type' => 'ktp',
                'file' => $file,
            ])
            ->assertStatus(422)
            ->assertJsonPath('success', false)
            ->assertJsonPath('error.code', 'VALIDATION_ERROR');

        // A rejected upload must not leave a row or a stored file behind.
        $this->assertDatabaseCount('application_documents', 0);
        $this->assertSame([], Storage::disk('local')->allFiles());
    }

    public function test_upload_rejects_file_above_size_limit(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create([
            'applicant_id' => $profile->id,
            'status' => 'draft',
        ]);

        // The endpoint allows 10 MB; 11 MB must be refused.
        $file = UploadedFile::fake()->create('ijazah_besar.pdf', 11 * 1024, 'application/pdf');

        $this->actingAs($user, 'sanctum')
            ->postJson("/api/v1/applications/{$app->id}/documents", [
                'document_type' => 'diploma',
                'file' => $file,
            ])
            ->assertStatus(422)
            ->assertJsonPath('success', false)
            ->assertJsonPath('error.code', 'VALIDATION_ERROR');

        $this->assertDatabaseCount('application_documents', 0);
        $this->assertSame([], Storage::disk('local')->allFiles());
    }

    public function test_delete_removes_document_when_editable(): void
    {
        $user = $this->createApplicantUser();
        $profile = ApplicantProfile::factory()->create(['user_id' => $user->id]);
        $app = Application::factory()->create([
            'applicant_id' => $profile->id,
            'status' => 'draft',
        ]);

        $filePath = "documents/{$app->id}/doc_delete.pdf";
        Storage::disk('local')->put($filePath, 'konten file');

        $doc = ApplicationDocument::factory()->create([
            'application_id' => $app->id,
            'file_path' => $filePath,
        ]);

        $this->actingAs($user, 'sanctum')
            ->deleteJson("/api/v1/applications/{$app->id}/documents/{$doc->id}")
            ->assertStatus(200)
            ->assertJsonPath('success', true);

        $this->assertDatabaseMissing('application_documents', ['id' => $doc->id]);
        Storage::disk('local')->assertMissing($filePath);
    }
}
