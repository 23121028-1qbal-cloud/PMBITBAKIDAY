<?php

declare(strict_types=1);

/*
 * PMB ITB Akiday — one-off data migration: SQLite -> MySQL.
 *
 * The MySQL schema must already exist (run `php artisan migrate` against the
 * MySQL database first); this script only copies rows, preserving primary keys
 * and foreign keys exactly as stored. It never truncates and never seeds.
 *
 *   DB_CONNECTION=mysql DB_HOST=... php scripts/migrate-sqlite-to-mysql.php [source.sqlite]
 *
 * Safety rules enforced here:
 *   - refuses to run when the target connection is still sqlite;
 *   - refuses to run when the target already contains rows (unless --force);
 *   - never deletes or modifies the source file;
 *   - verifies row counts per table before reporting success.
 */

$projectRoot = dirname(__DIR__);
$backendDir = $projectRoot.'/backend-api';

require $backendDir.'/vendor/autoload.php';

/** @var \Illuminate\Foundation\Application $app */
$app = require $backendDir.'/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

$sourcePath = $argv[1] ?? $backendDir.'/database/database.sqlite';
$force = in_array('--force', $argv, true);

if (! is_file($sourcePath)) {
    fwrite(STDERR, "source database not found: {$sourcePath}\n");
    exit(3);
}

$target = DB::connection();
$targetDriver = $target->getDriverName();

if ($targetDriver === 'sqlite') {
    fwrite(STDERR, "REFUSING: the target connection is still sqlite. Point DB_CONNECTION at mysql first.\n");
    exit(4);
}

config(['database.connections.stage_source' => [
    'driver' => 'sqlite',
    'database' => $sourcePath,
    'prefix' => '',
    'foreign_key_constraints' => false,
]]);

$source = DB::connection('stage_source');

echo "Source : {$sourcePath}\n";
echo "Target : {$targetDriver} / ".$target->getDatabaseName()."\n\n";

$sourceTables = array_map(
    'strval',
    collect($source->select("select name from sqlite_master where type='table' and name not like 'sqlite_%' order by name"))
        ->pluck('name')->all()
);

$targetTables = array_map(
    'strval',
    collect($target->select(
        'select table_name as name from information_schema.tables where table_schema = database() order by table_name'
    ))->pluck('name')->all()
);

$missing = array_values(array_diff($sourceTables, $targetTables));

if ($missing !== []) {
    fwrite(STDERR, "REFUSING: tables missing from the MySQL schema (run migrations first): ".implode(', ', $missing)."\n");
    exit(5);
}

// `migrations` is owned by the migration runner; its rows are compared instead
// of copied, so the schema state is never silently rewritten.
$copyTables = array_values(array_diff($sourceTables, ['migrations', 'sqlite_sequence']));

$nonEmpty = [];

foreach ($copyTables as $table) {
    $rows = (int) $target->table($table)->count();

    if ($rows > 0) {
        $nonEmpty[$table] = $rows;
    }
}

if ($nonEmpty !== [] && ! $force) {
    fwrite(STDERR, "REFUSING: the MySQL target is not empty:\n");

    foreach ($nonEmpty as $table => $rows) {
        fwrite(STDERR, "  - {$table}: {$rows} rows\n");
    }

    fwrite(STDERR, "Re-run with --force only after you are sure the target should be overwritten table by table.\n");
    exit(6);
}

$target->statement('SET FOREIGN_KEY_CHECKS=0');
$target->beginTransaction();

$inserted = [];

try {
    foreach ($copyTables as $table) {
        $targetColumns = Schema::connection(null)->getColumnListing($table);
        $rows = $force ? $target->table($table)->count() : 0;

        if ($force && $rows > 0) {
            // --force replaces target rows for this table; the source is untouched.
            $target->table($table)->delete();
        }

        $sourceColumns = array_map(
            'strval',
            collect($source->select('PRAGMA table_info("'.$table.'")'))->pluck('name')->all()
        );

        $columns = array_values(array_intersect($sourceColumns, $targetColumns));

        if ($columns === []) {
            fwrite(STDERR, "skipping {$table}: no shared columns\n");
            $inserted[$table] = 0;

            continue;
        }

        $written = 0;

        $source->table($table)->orderBy($columns[0])->chunk(200, function ($chunk) use ($target, $table, $columns, &$written) {
            $payload = [];

            foreach ($chunk as $row) {
                $values = (array) $row;
                $record = [];

                foreach ($columns as $column) {
                    $record[$column] = $values[$column] ?? null;
                }

                $payload[] = $record;
            }

            if ($payload !== []) {
                $target->table($table)->insert($payload);
                $written += count($payload);
            }
        });

        $inserted[$table] = $written;
    }

    $target->commit();
} catch (Throwable $e) {
    $target->rollBack();
    $target->statement('SET FOREIGN_KEY_CHECKS=1');
    fwrite(STDERR, "migration failed: ".$e->getMessage()."\n");
    exit(7);
}

$target->statement('SET FOREIGN_KEY_CHECKS=1');

// --- Migration state comparison ---------------------------------------------

$sourceMigrations = array_map('strval', $source->table('migrations')->orderBy('id')->pluck('migration')->all());
$targetMigrations = array_map('strval', $target->table('migrations')->orderBy('id')->pluck('migration')->all());


echo "Migrations in source : ".count($sourceMigrations)."\n";
echo "Migrations in target : ".count($targetMigrations)."\n";

if ($sourceMigrations !== $targetMigrations) {
    echo "WARNING: migration lists differ (source vs target order/names).\n";
}

// --- Verification ------------------------------------------------------------

echo "\n".str_pad('table', 34).str_pad('source', 9).str_pad('target', 9)."status\n";
echo str_repeat('-', 60)."\n";

$failures = 0;

foreach ($sourceTables as $table) {
    $sourceCount = (int) $source->table($table)->count();
    $targetCount = (int) $target->table($table)->count();
    $ok = $sourceCount === $targetCount;

    if (! $ok) {
        $failures++;
    }

    printf(
        "%s%s%s%s\n",
        str_pad($table, 34),
        str_pad((string) $sourceCount, 9),
        str_pad((string) $targetCount, 9),
        $ok ? 'OK' : 'MISMATCH',
    );
}

echo "\n";

if ($failures > 0) {
    fwrite(STDERR, "VERIFICATION FAILED: {$failures} table(s) differ. Do not cut over yet.\n");
    exit(8);
}

echo "VERIFICATION PASSED: every table has identical row counts.\n";
