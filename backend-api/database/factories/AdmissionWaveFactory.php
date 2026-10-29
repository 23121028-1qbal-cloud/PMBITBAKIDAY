<?php

namespace Database\Factories;

use App\Models\AdmissionWave;
use Illuminate\Database\Eloquent\Factories\Factory;

class AdmissionWaveFactory extends Factory
{
    protected $model = AdmissionWave::class;

    public function definition(): array
    {
        return [
            'name' => 'Gelombang '.fake()->numberBetween(1, 4),
            'start_date' => now()->subDay(),
            'end_date' => now()->addMonth(),
            'is_active' => true,
        ];
    }

    public function upcoming(): static
    {
        return $this->state(fn () => [
            'start_date' => now()->addWeek(),
            'end_date' => now()->addMonths(2),
        ]);
    }

    public function closed(): static
    {
        return $this->state(fn () => [
            'start_date' => now()->subMonths(2),
            'end_date' => now()->subDay(),
        ]);
    }

    public function inactive(): static
    {
        return $this->state(fn () => ['is_active' => false]);
    }
}
