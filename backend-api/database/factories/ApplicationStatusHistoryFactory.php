<?php

namespace Database\Factories;

use App\Models\Application;
use App\Models\ApplicationStatusHistory;
use App\Models\User;
use Illuminate\Database\Eloquent\Factories\Factory;

class ApplicationStatusHistoryFactory extends Factory
{
    protected $model = ApplicationStatusHistory::class;

    public function definition(): array
    {
        return [
            'application_id' => Application::factory(),
            'from_status' => null,
            'to_status' => 'draft',
            'note' => 'Draft pendaftaran dibuat.',
            'changed_by_user_id' => User::factory(),
            'created_at' => now(),
        ];
    }
}
