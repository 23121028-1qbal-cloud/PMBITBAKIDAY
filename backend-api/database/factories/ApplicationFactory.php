<?php

namespace Database\Factories;

use App\Models\Application;
use App\Models\ApplicantProfile;
use App\Models\AdmissionWave;
use App\Models\StudyProgram;
use Illuminate\Database\Eloquent\Factories\Factory;

class ApplicationFactory extends Factory
{
    protected $model = Application::class;

    public function definition(): array
    {
        return [
            'applicant_id' => ApplicantProfile::factory(),
            'wave_id' => AdmissionWave::factory(),
            'study_program_id' => StudyProgram::factory(),
            'registration_number' => 'PMB-2026-' . fake()->unique()->numerify('######'),
            'status' => 'draft',
            'submitted_at' => null,
        ];
    }

    public function submitted(): static
    {
        return $this->state(fn () => [
            'status' => 'submitted',
            'submitted_at' => now(),
        ]);
    }

    public function review(): static
    {
        return $this->state(fn () => [
            'status' => 'review',
            'submitted_at' => now(),
        ]);
    }

    public function revision(): static
    {
        return $this->state(fn () => [
            'status' => 'revision',
            'submitted_at' => now(),
        ]);
    }

    public function accepted(): static
    {
        return $this->state(fn () => [
            'status' => 'accepted',
            'submitted_at' => now(),
        ]);
    }

    public function rejected(): static
    {
        return $this->state(fn () => [
            'status' => 'rejected',
            'submitted_at' => now(),
        ]);
    }
}