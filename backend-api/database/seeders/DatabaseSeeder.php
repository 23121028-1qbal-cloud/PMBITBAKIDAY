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

        // Development-only staff accounts. Credentials come from the
        // environment so no secret is committed to the repository, and any
        // account whose password is unset is skipped entirely.
        $this->seedStaffUser('admin', 'SEED_ADMIN_EMAIL', 'admin@example.com', 'SEED_ADMIN_PASSWORD');
        $this->seedStaffUser('panitia', 'SEED_PANITIA_EMAIL', 'panitia@example.com', 'SEED_PANITIA_PASSWORD');
        $this->seedStaffUser('pimpinan', 'SEED_PIMPINAN_EMAIL', 'pimpinan@example.com', 'SEED_PIMPINAN_PASSWORD');
    }

    /**
     * Create or update one staff account, using updateOrCreate so re-running the
     * seeder is idempotent and also re-hashes a rotated password.
     */
    private function seedStaffUser(string $role, string $emailKey, string $defaultEmail, string $passwordKey): void
    {
        $password = env($passwordKey);

        if ($password === null || $password === '') {
            return;
        }

        $roleModel = Role::query()->where('name', $role)->first();

        if (! $roleModel) {
            return;
        }

        User::query()->updateOrCreate(
            ['email' => env($emailKey, $defaultEmail)],
            [
                'role_id' => $roleModel->id,
                'password' => Hash::make($password),
                'is_active' => true,
            ],
        );
    }
}
