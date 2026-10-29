<?php

namespace Database\Seeders;

use App\Models\Role;
use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        $this->call(RoleSeeder::class);

        // Development-only admin account. Credentials come from the
        // environment so no secret is committed to the repository.
        $email = env('SEED_ADMIN_EMAIL', 'admin@example.com');
        $password = env('SEED_ADMIN_PASSWORD');

        if ($password === null) {
            return;
        }

        $adminRole = Role::query()->where('name', 'admin')->first();

        if (! $adminRole) {
            return;
        }

        User::query()->updateOrCreate(
            ['email' => $email],
            [
                'role_id' => $adminRole->id,
                'password' => Hash::make($password),
                'is_active' => true,
            ],
        );
    }
}
