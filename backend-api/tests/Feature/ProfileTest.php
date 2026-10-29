<?php

namespace Tests\Feature;

use App\Models\ApplicantProfile;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ProfileTest extends TestCase
{
    use RefreshDatabase;

    private function validProfile(): array
    {
        return [
            'full_name' => 'Nama Contoh',
            'nik' => '3200000000000001',
            'birth_place' => 'Bandung',
            'birth_date' => '2007-05-10',
            'gender' => 'L',
            'phone' => '081234567890',
            'address' => 'Alamat tempat tinggal',
        ];
    }

    public function test_profile_requires_login(): void
    {
        $this->getJson('/api/v1/applicant/profile')
            ->assertStatus(401)
            ->assertJsonPath('error.code', 'UNAUTHENTICATED');
    }

    public function test_missing_profile_returns_not_found_envelope(): void
    {
        $this->actingAs(User::factory()->create(), 'sanctum')
            ->getJson('/api/v1/applicant/profile')
            ->assertStatus(404)
            ->assertJsonPath('error.code', 'RESOURCE_NOT_FOUND');
    }

    public function test_profile_is_scoped_to_the_authenticated_account(): void
    {
        $me = User::factory()->create();
        ApplicantProfile::factory()->create([
            'user_id' => $me->id,
            'full_name' => 'Profil Milik Saya',
            'birth_date' => '2007-05-10',
        ]);
        ApplicantProfile::factory()->create(['full_name' => 'Profil Orang Lain']);

        $this->actingAs($me, 'sanctum')
            ->getJson('/api/v1/applicant/profile')
            ->assertStatus(200)
            ->assertJsonPath('message', 'Profil berhasil diambil.')
            ->assertJsonPath('data.user_id', $me->id)
            ->assertJsonPath('data.full_name', 'Profil Milik Saya')
            ->assertJsonPath('data.birth_date', '2007-05-10');
    }

    public function test_put_creates_the_profile_when_it_does_not_exist(): void
    {
        $me = User::factory()->create();

        $response = $this->actingAs($me, 'sanctum')
            ->putJson('/api/v1/applicant/profile', $this->validProfile());

        $response->assertStatus(200)
            ->assertJsonPath('message', 'Profil berhasil disimpan.')
            ->assertJsonPath('data.full_name', 'Nama Contoh');

        $this->assertDatabaseHas('applicant_profiles', [
            'user_id' => $me->id,
            'nik' => '3200000000000001',
        ]);
    }

    public function test_put_updates_without_creating_a_second_profile(): void
    {
        $me = User::factory()->create();
        ApplicantProfile::factory()->create(['user_id' => $me->id]);

        $payload = $this->validProfile();
        $payload['full_name'] = 'Nama Yang Diperbarui';

        $this->actingAs($me, 'sanctum')
            ->putJson('/api/v1/applicant/profile', $payload)
            ->assertStatus(200)
            ->assertJsonPath('data.full_name', 'Nama Yang Diperbarui');

        $this->assertSame(1, ApplicantProfile::query()->where('user_id', $me->id)->count());
    }

    public function test_invalid_nik_is_rejected_with_documented_envelope(): void
    {
        $me = User::factory()->create();
        $payload = $this->validProfile();
        $payload['nik'] = '123';

        $this->actingAs($me, 'sanctum')
            ->putJson('/api/v1/applicant/profile', $payload)
            ->assertStatus(422)
            ->assertJsonPath('success', false)
            ->assertJsonPath('error.code', 'VALIDATION_ERROR')
            ->assertJsonStructure(['error' => ['code', 'details' => ['nik']]]);

        $this->assertDatabaseMissing('applicant_profiles', ['user_id' => $me->id]);
    }

    public function test_empty_required_fields_are_rejected_in_indonesian(): void
    {
        $me = User::factory()->create();

        $this->actingAs($me, 'sanctum')
            ->putJson('/api/v1/applicant/profile', ['nik' => '3200000000000001'])
            ->assertStatus(422)
            ->assertJsonPath('error.code', 'VALIDATION_ERROR')
            ->assertJsonPath('error.details.full_name.0', 'Bidang nama lengkap wajib diisi.');
    }

    public function test_gender_must_be_one_of_the_form_choices(): void
    {
        $me = User::factory()->create();
        $payload = $this->validProfile();
        $payload['gender'] = 'X';

        $this->actingAs($me, 'sanctum')
            ->putJson('/api/v1/applicant/profile', $payload)
            ->assertStatus(422)
            ->assertJsonPath('error.code', 'VALIDATION_ERROR');
    }
}
