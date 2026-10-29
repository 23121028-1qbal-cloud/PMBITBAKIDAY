<?php

declare(strict_types=1);

/*
 * Tahap 11 helper — reads the database/filesystem configuration that Laravel
 * actually resolves, without printing credentials.
 *
 *   php scripts/stage11-db.php info
 *   php scripts/stage11-db.php dump <target-file>
 *   php scripts/stage11-db.php counts [<sqlite-file>]
 *   php scripts/stage11-db.php document-refs [<sqlite-file>]
 *
 * `info` prints shell-eval-able KEY='value' lines; nothing secret is included.
 * `dump` supports sqlite (consistent VACUUM INTO snapshot) and MySQL/MariaDB
 * (mysqldump via a temporary --defaults-extra-file, so the password never
 * appears in the process list or in the shell history).
 */

$projectRoot = dirname(__DIR__);
$backendDir = $projectRoot.'/backend-api';

require $backendDir.'/vendor/autoload.php';

/** @var \Illuminate\Foundation\Application $app */
$app = require $backendDir.'/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

$command = $argv[1] ?? '';

$connectionName = (string) config('database.default');
$connection = (array) config("database.connections.{$connectionName}", []);
$driver = (string) ($connection['driver'] ?? $connectionName);

function shellLine(string $key, string $value): void
{
    printf("%s=%s\n", $key, escapeshellarg($value));
}

/**
 * PDO handle for either the configured connection or an isolated sqlite file.
 */
function pdoFor(?string $sqlitePath): PDO
{
    if ($sqlitePath !== null) {
        if (! is_file($sqlitePath)) {
            fwrite(STDERR, "sqlite file not found: {$sqlitePath}\n");
            exit(3);
        }

        return new PDO('sqlite:'.$sqlitePath, null, null, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        ]);
    }

    return Illuminate\Support\Facades\DB::connection()->getPdo();
}

/**
 * Write a 0600 MySQL client option file so tools can authenticate without the
 * password appearing on a command line.
 *
 * @param  array<string, mixed>  $connection
 */
function writeClientConfig(array $connection): string
{
    $configFile = tempnam(sys_get_temp_dir(), 'pmb-client-');

    if ($configFile === false) {
        fwrite(STDERR, "unable to create temporary credentials file\n");
        exit(6);
    }

    chmod($configFile, 0600);
    $password = str_replace(['\\', '"'], ['\\\\', '\\"'], (string) ($connection['password'] ?? ''));

    file_put_contents($configFile, "[client]\n"
        .'host='.(string) ($connection['host'] ?? '127.0.0.1')."\n"
        .'port='.(string) ($connection['port'] ?? '3306')."\n"
        .'user='.(string) ($connection['username'] ?? '')."\n"
        .'password="'.$password.'"'."\n"
        ."default-character-set=utf8mb4\n");

    return $configFile;
}

/**
 * Table names visible in the given database handle.
 *
 * @return list<string>
 */
function tableNames(PDO $pdo): array
{
    $driverName = $pdo->getAttribute(PDO::ATTR_DRIVER_NAME);

    if ($driverName === 'sqlite') {
        $rows = $pdo->query(
            "select name from sqlite_master where type='table' and name not like 'sqlite_%' order by name"
        )->fetchAll(PDO::FETCH_COLUMN);

        return array_values(array_map('strval', $rows));
    }

    $rows = $pdo->query(
        'select table_name from information_schema.tables where table_schema = database() order by table_name'
    )->fetchAll(PDO::FETCH_COLUMN);

    return array_values(array_map('strval', $rows));
}

switch ($command) {
    case 'info':
        shellLine('PMB_DB_CONNECTION', $connectionName);
        shellLine('PMB_DB_DRIVER', $driver);
        shellLine('PMB_DB_HOST', (string) ($connection['host'] ?? ''));
        shellLine('PMB_DB_PORT', (string) ($connection['port'] ?? ''));
        shellLine('PMB_DB_NAME', (string) ($connection['database'] ?? ''));
        shellLine('PMB_DB_SQLITE_PATH', $driver === 'sqlite' ? (string) ($connection['database'] ?? '') : '');
        shellLine('PMB_PRIVATE_STORAGE', (string) config('filesystems.disks.local.root'));
        shellLine('PMB_APP_ENV', (string) config('app.env'));
        shellLine('PMB_FRAMEWORK_VERSION', $app->version());
        break;

    case 'dump':
        $target = $argv[2] ?? '';

        if ($target === '') {
            fwrite(STDERR, "usage: dump <target-file>\n");
            exit(2);
        }

        if (file_exists($target)) {
            fwrite(STDERR, "refusing to overwrite existing file: {$target}\n");
            exit(4);
        }

        if ($driver === 'sqlite') {
            $source = (string) ($connection['database'] ?? '');

            if (! is_file($source)) {
                fwrite(STDERR, "sqlite database not found: {$source}\n");
                exit(3);
            }

            $pdo = new PDO('sqlite:'.$source, null, null, [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]);
            // VACUUM INTO writes a transactionally consistent copy of the database.
            $pdo->exec('VACUUM INTO '.$pdo->quote($target));

            echo "driver=sqlite\n";
            echo 'database='.basename($source)."\n";
            break;
        }

        $binary = null;

        foreach (['mysqldump', 'mariadb-dump'] as $candidate) {
            $found = trim((string) shell_exec('command -v '.escapeshellarg($candidate).' 2>/dev/null'));

            if ($found !== '') {
                $binary = $found;
                break;
            }
        }

        if ($binary === null) {
            fwrite(STDERR, "mysqldump/mariadb-dump not found in PATH\n");
            exit(5);
        }        $configFile = writeClientConfig($connection);

        $database = (string) ($connection['database'] ?? '');
        // Deliberately no --databases: that option embeds CREATE DATABASE and
        // USE, which would make a restore write into the original database
        // name instead of the isolated target chosen by the operator.
        $commandLine = sprintf(
            '%s --defaults-extra-file=%s --single-transaction --quick --routines --triggers --no-tablespaces %s > %s 2>%s',
            escapeshellarg($binary),
            escapeshellarg($configFile),
            escapeshellarg($database),
            escapeshellarg($target),
            escapeshellarg($target.'.err'),
        );

        exec($commandLine, $output, $exitCode);
        @unlink($configFile);

        $errorOutput = is_file($target.'.err') ? trim((string) file_get_contents($target.'.err')) : '';
        @unlink($target.'.err');

        if ($exitCode !== 0) {
            fwrite(STDERR, "dump failed (exit {$exitCode}): {$errorOutput}\n");
            exit(7);
        }

        echo "driver={$driver}\n";
        echo "database={$database}\n";
        break;

    case 'env-for':
        // Points Laravel at an alternative database (a restored copy) without
        // printing the password: the values go into a 0600 file the caller
        // sources and then deletes.
        $database = $argv[2] ?? '';

        if ($database === '') {
            fwrite(STDERR, "usage: env-for <database-path-or-name>\n");
            exit(2);
        }

        $envFile = tempnam(sys_get_temp_dir(), 'pmb-env-');

        if ($envFile === false) {
            fwrite(STDERR, "unable to create temporary env file\n");
            exit(6);
        }

        chmod($envFile, 0600);
        $exported = ['export DB_URL='];
        $exported[] = 'export DB_CONNECTION='.escapeshellarg($connectionName);
        $exported[] = 'export DB_DATABASE='.escapeshellarg($database);

        if ($driver !== 'sqlite') {
            $exported[] = 'export DB_HOST='.escapeshellarg((string) ($connection['host'] ?? '127.0.0.1'));
            $exported[] = 'export DB_PORT='.escapeshellarg((string) ($connection['port'] ?? '3306'));
            $exported[] = 'export DB_USERNAME='.escapeshellarg((string) ($connection['username'] ?? ''));
            $exported[] = 'export DB_PASSWORD='.escapeshellarg((string) ($connection['password'] ?? ''));
        }

        file_put_contents($envFile, implode("\n", $exported)."\n");
        echo $envFile, "\n";
        break;

    case 'admin-creds':
        // Client option file for an administrative account, read from the
        // environment so the password never appears in the process list.
        $adminUser = getenv('PMB_RESTORE_DB_ADMIN_USER');
        $adminPassword = getenv('PMB_RESTORE_DB_ADMIN_PASSWORD');

        if ($adminUser === false || $adminUser === '') {
            fwrite(STDERR, "PMB_RESTORE_DB_ADMIN_USER is not set\n");
            exit(3);
        }

        echo writeClientConfig([
            'host' => $connection['host'] ?? '127.0.0.1',
            'port' => $connection['port'] ?? '3306',
            'username' => $adminUser,
            'password' => $adminPassword === false ? '' : $adminPassword,
        ]), "\n";
        break;

    case 'creds':
        // Used by scripts/restore-verify.sh to import a dump with the mysql
        // client. The caller is responsible for deleting the printed file.
        if ($driver === 'sqlite') {
            fwrite(STDERR, "sqlite connection has no client credentials file\n");
            exit(3);
        }

        echo writeClientConfig($connection), "\n";
        break;

    case 'http-fixtures':
        // Driver-agnostic fixture lookup for restore-verify.sh: the first
        // application document, its owning account, and a different applicant.
        $pdo = pdoFor(null);

        if (! in_array('application_documents', tableNames($pdo), true)) {
            break;
        }

        $row = $pdo->query(
            'select d.application_id as app_id, d.id as doc_id, d.file_path as file_path,'
            .' (select p.user_id from applicant_profiles p'
            .'    join applications a on a.applicant_id = p.id where a.id = d.application_id) as owner_id'
            .' from application_documents d order by d.id limit 1'
        )->fetch(PDO::FETCH_ASSOC);

        if (! $row) {
            break;
        }

        $other = $pdo->prepare('select user_id from applicant_profiles where user_id <> ? order by user_id limit 1');
        $other->execute([(int) $row['owner_id']]);
        $otherId = $other->fetchColumn();

        printf(
            "%s\t%s\t%s\t%s\t%s\n",
            $row['app_id'],
            $row['doc_id'],
            $row['file_path'],
            $row['owner_id'],
            $otherId === false ? '' : $otherId,
        );
        break;

    case 'mint-token':
        // Issues a Sanctum token for a user in the database currently selected
        // by the environment (used only against a restored copy).
        $userId = (int) ($argv[2] ?? 0);

        if ($userId <= 0) {
            fwrite(STDERR, "usage: mint-token <user-id>\n");
            exit(2);
        }

        $plain = bin2hex(random_bytes(20));
        $id = Illuminate\Support\Facades\DB::table('personal_access_tokens')->insertGetId([
            'tokenable_type' => "App\\Models\\User",
            'tokenable_id' => $userId,
            'name' => 'restore-verify',
            'token' => hash('sha256', $plain),
            'abilities' => '["*"]',
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        echo $id.'|'.$plain;
        break;

    case 'counts':
        $pdo = pdoFor($argv[2] ?? null);
        $counts = [];

        foreach (tableNames($pdo) as $table) {
            $quoted = $pdo->getAttribute(PDO::ATTR_DRIVER_NAME) === 'sqlite'
                ? '"'.$table.'"'
                : '`'.$table.'`';
            $counts[$table] = (int) $pdo->query("select count(*) from {$quoted}")->fetchColumn();
        }

        echo json_encode($counts, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES), "\n";
        break;

    case 'document-refs':
        $pdo = pdoFor($argv[2] ?? null);
        $hasTable = in_array('application_documents', tableNames($pdo), true);

        if (! $hasTable) {
            break;
        }

        $rows = $pdo->query(
            'select application_id, document_type, file_path, file_size from application_documents order by id'
        )->fetchAll(PDO::FETCH_ASSOC);

        foreach ($rows as $row) {
            printf(
                "%s\t%s\t%s\t%s\n",
                $row['application_id'],
                $row['document_type'],
                $row['file_path'],
                $row['file_size'],
            );
        }

        break;

    default:
        fwrite(STDERR, "unknown command; use info, dump, counts or document-refs\n");
        exit(2);
}
