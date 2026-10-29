<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('applicant_profiles', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->unique()->constrained('users')->cascadeOnDelete();
            $table->string('full_name', 150);
            $table->string('nik', 16);
            $table->string('birth_place', 100);
            $table->date('birth_date');
            $table->string('gender', 20);
            $table->string('phone', 20);
            $table->text('address');
            $table->timestamps();

            // NIK is intentionally NOT unique here: the uniqueness policy is
            // still an open decision (DB-03). Do not add it silently.
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('applicant_profiles');
    }
};
