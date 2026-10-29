<?php

namespace Database\Seeders;

use App\Models\Role;
use Illuminate\Database\Seeder;

class RoleSeeder extends Seeder
{
    /**
     * Seed the baseline roles documented in the Database Model.
     *
     * Uses updateOrCreate so the seeder is idempotent and safe to re-run.
     */
    public function run(): void
    {
        foreach (['calon_mahasiswa', 'admin', 'panitia', 'pimpinan'] as $name) {
            Role::query()->updateOrCreate(['name' => $name]);
        }
    }
}
