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
        Schema::create('applications', function (Blueprint $table) {
            $table->id();
            $table->foreignId('applicant_id')->constrained('applicant_profiles')->restrictOnDelete();
            $table->foreignId('wave_id')->constrained('admission_waves')->restrictOnDelete();
            $table->foreignId('study_program_id')->constrained('study_programs')->restrictOnDelete();
            $table->string('registration_number', 50)->unique();
            $table->string('status', 20)->default('draft');
            $table->dateTime('submitted_at')->nullable();
            $table->timestamps();

            // Blocks a second application in the same wave, including under
            // concurrent requests where application-level checks can race.
            $table->unique(['applicant_id', 'wave_id']);

            $table->index('status');
            $table->index('wave_id');
            $table->index('study_program_id');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('applications');
    }
};
