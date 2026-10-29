<?php

namespace Database\Factories;

use App\Models\Application;
use App\Models\ApplicationDocument;
use Illuminate\Database\Eloquent\Factories\Factory;

class ApplicationDocumentFactory extends Factory
{
    protected $model = ApplicationDocument::class;

    public function definition(): array
    {
        return [
            'application_id' => Application::factory(),
            'document_type' => fake()->randomElement(['photo', 'diploma']),
            'file_path' => 'documents/1/doc_test.pdf',
            'mime_type' => 'application/pdf',
            'file_size' => fake()->numberBetween(10000, 500000),
            'uploaded_at' => now(),
        ];
    }
}
