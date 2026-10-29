<?php

namespace Database\Factories;

use App\Models\ApplicantProfile;
use App\Models\User;
use Illuminate\Database\Eloquent\Factories\Factory;

class ApplicantProfileFactory extends Factory
{
    protected $model = ApplicantProfile::class;

    public function definition(): array
    {
        return [
            'user_id' => User::factory(),
            'full_name' => fake()->name(),
            // NIK is exactly 16 digits and the column is string(16); generating
            // more characters silently passes on SQLite but is truncated by
            // MySQL/MariaDB, so the length must be correct here.
            'nik' => fake()->numerify('32##############'),
            'birth_place' => fake()->city(),
            'birth_date' => fake()->dateTimeBetween('-25 years', '-15 years')->format('Y-m-d'),
            'gender' => fake()->randomElement(['L', 'P']),
            'phone' => fake()->numerify('08##########'),
            'address' => 'Alamat contoh, Kota Contoh',
        ];
    }
}
