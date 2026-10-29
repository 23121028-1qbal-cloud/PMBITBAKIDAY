<?php

namespace Tests\Feature;

use App\Models\AdmissionWave;
use App\Models\ApplicantProfile;
use App\Models\Application;
use App\Models\Role;
use App\Models\StudyProgram;
use App\Models\User;
use Illuminate\Database\QueryException;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

/**
 * Covers TC-DB-001 (foreign key integrity) and TC-DB-002 (unique constraint)
 * from Test Cases, which were blocked in Tahap 3 for lack of a test database.
 */
class DatabaseIntegrityTest extends TestCase
{
    use RefreshDatabase;

    private function applicant(): ApplicantProfile
    {
        $user = User::factory()->create();

        return ApplicantProfile::query()->create([
            'user_id' => $user->id,
            'full_name' => 'Nama Contoh',
            'nik' => '3200000000000001',
            'birth_place' => 'Bandung',
            'birth_date' => '2007-05-10',
            'gender' => 'L',
            'phone' => '081234567890',
            'address' => 'Alamat contoh',
        ]);
    }

    private function wave(): AdmissionWave
    {
        return AdmissionWave::query()->create([
            'name' => 'Gelombang 1',
            'start_date' => '2026-11-01 00:00:00',
            'end_date' => '2026-12-15 23:59:59',
            'is_active' => true,
        ]);
    }

    private function program(): StudyProgram
    {
        return StudyProgram::query()->create([
            'code' => 'IF',
            'name' => 'Informatika',
            'is_active' => true,
        ]);
    }

    public function test_application_requires_existing_parent_rows(): void
    {
        $this->expectException(QueryException::class);

        Application::query()->create([
            'applicant_id' => 999999,
            'wave_id' => 999999,
            'study_program_id' => 999999,
            'registration_number' => 'PMB-2026-000001',
            'status' => 'draft',
        ]);
    }

    public function test_second_application_in_same_wave_is_rejected(): void
    {
        $applicant = $this->applicant();
        $wave = $this->wave();
        $program = $this->program();

        Application::query()->create([
            'applicant_id' => $applicant->id,
            'wave_id' => $wave->id,
            'study_program_id' => $program->id,
            'registration_number' => 'PMB-2026-000001',
            'status' => 'draft',
        ]);

        $this->expectException(QueryException::class);

        Application::query()->create([
            'applicant_id' => $applicant->id,
            'wave_id' => $wave->id,
            'study_program_id' => $program->id,
            'registration_number' => 'PMB-2026-000002',
            'status' => 'draft',
        ]);
    }

    public function test_same_applicant_may_apply_to_a_different_wave(): void
    {
        $applicant = $this->applicant();
        $program = $this->program();

        foreach (['Gelombang 1', 'Gelombang 2'] as $index => $name) {
            $wave = AdmissionWave::query()->create([
                'name' => $name,
                'start_date' => '2026-11-01 00:00:00',
                'end_date' => '2026-12-15 23:59:59',
                'is_active' => true,
            ]);

            Application::query()->create([
                'applicant_id' => $applicant->id,
                'wave_id' => $wave->id,
                'study_program_id' => $program->id,
                'registration_number' => sprintf('PMB-2026-00000%d', $index + 1),
                'status' => 'draft',
            ]);
        }

        $this->assertSame(2, Application::query()->count());
    }

    public function test_duplicate_role_name_is_rejected(): void
    {
        Role::query()->create(['name' => 'admin']);

        $this->expectException(QueryException::class);

        Role::query()->create(['name' => 'admin']);
    }

    public function test_status_history_actor_cannot_be_silently_dropped(): void
    {
        $applicant = $this->applicant();
        $wave = $this->wave();
        $program = $this->program();

        $application = Application::query()->create([
            'applicant_id' => $applicant->id,
            'wave_id' => $wave->id,
            'study_program_id' => $program->id,
            'registration_number' => 'PMB-2026-000001',
            'status' => 'draft',
        ]);

        $actor = User::factory()->create();

        $application->statusHistories()->create([
            'from_status' => null,
            'to_status' => 'draft',
            'note' => 'Pengajuan dibuat.',
            'changed_by_user_id' => $actor->id,
        ]);

        // RESTRICT keeps the audit trail: the referenced actor may not vanish.
        $this->expectException(QueryException::class);

        $actor->delete();
    }
}
