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

        // Development staff accounts. Default passwords are provided for
        // local development and documentation testing, and can be overridden
        // via environment variables in backend-api/.env.
        $this->seedStaffUser('admin', 'SEED_ADMIN_EMAIL', 'admin@example.com', 'SEED_ADMIN_PASSWORD', 'admin123');
        $this->seedStaffUser('panitia', 'SEED_PANITIA_EMAIL', 'panitia@example.com', 'SEED_PANITIA_PASSWORD', 'panitia123');
        $this->seedStaffUser('pimpinan', 'SEED_PIMPINAN_EMAIL', 'pimpinan@example.com', 'SEED_PIMPINAN_PASSWORD', 'pimpinan123');
    }

    /**
     * Create or update one staff account, using updateOrCreate so re-running the
     * seeder is idempotent and also re-hashes a rotated password.
     */
    private function seedStaffUser(
        string $role,
        string $emailKey,
        string $defaultEmail,
        string $passwordKey,
        string $defaultPassword = 'password123'
    ): void {
        $password = env($passwordKey, $defaultPassword);

        if ($password === null || $password === '') {
            $password = $defaultPassword;
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
