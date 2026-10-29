<?php

namespace Database\Factories;

use App\Models\StudyProgram;
use Illuminate\Database\Eloquent\Factories\Factory;

class StudyProgramFactory extends Factory
{
    protected $model = StudyProgram::class;

    public function definition(): array
    {
        return [
            'code' => strtoupper(fake()->unique()->bothify('??##')),
            'name' => 'Program Studi Uji '.fake()->unique()->numberBetween(1, 9999),
            'is_active' => true,
        ];
    }

    public function inactive(): static
    {
        return $this->state(fn () => ['is_active' => false]);
    }
}
