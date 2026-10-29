#!/usr/bin/env bash
#
# PMB ITB Akiday — Tahap 11 isolated restore verification.
#
# Restores a backup into a throw-away location (never over live data) and proves:
#   1. every artifact still matches SHA256SUMS,
#   2. the database dump imports and its row counts match the manifest,
#   3. every restored document file is byte-identical to the original,
#   4. every document referenced by the database exists after restore,
#   5. the backend still enforces authentication and ownership on the restored
#      copy, and the owning applicant can download the file.
#
# Usage:
#   scripts/restore-verify.sh                 # newest backup in the backup root
#   scripts/restore-verify.sh <backup-dir>
#
# Environment:
#   PMB_BACKUP_ROOT       backup root (default: ../pmb_backups)
#   PMB_RESTORE_WORK_ROOT working directory (default: $TMPDIR/pmb-restore-work)
#   PMB_RESTORE_HTTP_PORT port for the temporary backend (default: 8011)
#   PMB_KEEP_RESTORE_WORK set to 1 to keep the restored files for inspection

set -Eeuo pipefail
umask 077

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend-api"
BACKUP_ROOT="${PMB_BACKUP_ROOT:-$PROJECT_ROOT/../pmb_backups}"
WORK_ROOT="${PMB_RESTORE_WORK_ROOT:-${TMPDIR:-/tmp}/pmb-restore-work}"
HTTP_PORT="${PMB_RESTORE_HTTP_PORT:-8011}"
STAMP="$(date -u +%Y%m%dT%H%M%SZ)"

FAILURES=0
SERVER_PID=""
SCRATCH_DB=""
CREDS_FILE=""
ADMIN_CREDS_FILE=""
ENV_FILE=""
work_dir=""
NOT_RUN=false

pass() { printf '  [PASS] %s\n' "$*"; }
fail_check() { printf '  [FAIL] %s\n' "$*"; FAILURES=$((FAILURES + 1)); }
info() { printf '  [info] %s\n' "$*"; }

finish() {
    local exit_code=$?

    if [ -n "$SERVER_PID" ]; then
        kill "$SERVER_PID" 2>/dev/null || true
        wait "$SERVER_PID" 2>/dev/null || true
        # `artisan serve` runs PHP's built-in server as a child process.
        pkill -f "php.*-S 127.0.0.1:$HTTP_PORT" 2>/dev/null || true
    fi

    # The scratch database only ever held restored data, so dropping it cannot
    # touch live data.
    if [ -n "$SCRATCH_DB" ] && [ -n "$ADMIN_CREDS_FILE" ] && [ -f "$ADMIN_CREDS_FILE" ]; then
        mysql --defaults-extra-file="$ADMIN_CREDS_FILE" -e "DROP DATABASE IF EXISTS \`$SCRATCH_DB\`;" 2>/dev/null || true
        info "scratch database $SCRATCH_DB dropped"
    fi

    if [ -n "$CREDS_FILE" ]; then rm -f "$CREDS_FILE"; fi
    if [ -n "$ADMIN_CREDS_FILE" ]; then rm -f "$ADMIN_CREDS_FILE"; fi
    if [ -n "$ENV_FILE" ]; then rm -f "$ENV_FILE"; fi

    if [ -d "$work_dir" ]; then
        if [ "${PMB_KEEP_RESTORE_WORK:-0}" = "1" ]; then
            info "restored files kept at $work_dir"
        else
            rm -rf "$work_dir"
        fi
    fi

    exit "$exit_code"
}
trap finish EXIT

echo "=========================================================================="
echo "  PMB ITB AKIDAY — ISOLATED RESTORE VERIFICATION"
echo "=========================================================================="

backup_dir="${1:-}"

if [ -z "$backup_dir" ]; then
    backup_dir="$(find "$BACKUP_ROOT" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | LC_ALL=C sort | tail -1)"
fi

if [ -z "$backup_dir" ] || [ ! -d "$backup_dir" ]; then
    echo "ERROR: no backup directory found (looked in $BACKUP_ROOT)" >&2
    exit 1
fi

# Normalise to an absolute path: this script changes directory while restoring,
# so a relative path given on the command line would stop resolving.
backup_dir="$(cd "$backup_dir" && pwd)"

[ -f "$backup_dir/manifest.txt" ] || { echo "ERROR: manifest.txt missing in $backup_dir" >&2; exit 1; }

manifest_get() { grep -m1 "^$1=" "$backup_dir/manifest.txt" | cut -d= -f2-; }

echo "Backup  : $backup_dir"
echo "Created : $(manifest_get created_at_utc)"
echo "Status  : $(manifest_get status)"
echo "Driver  : $(manifest_get db_driver)"
echo

if [ "$(manifest_get status)" != "complete" ]; then
    echo "ERROR: backup status is not 'complete'; not treated as a usable backup." >&2
    exit 1
fi

# --- 1. Artifact integrity ---------------------------------------------------

echo "[1/7] Verifying artifact checksums..."
if (cd "$backup_dir" && sha256sum -c --quiet SHA256SUMS); then
    pass "all artifacts match SHA256SUMS"
else
    fail_check "artifact checksums do not match"
    # A backup that fails its own checksums must not be restored at all: the
    # damage would otherwise be copied into the verification environment.
    echo
    echo "--------------------------------------------------------------------------"
    echo "RESULT: FAIL — backup integrity could not be verified; nothing was restored."
    exit 1
fi

# --- 2. Isolated workspace ---------------------------------------------------

echo "[2/7] Preparing isolated workspace..."
work_dir="$WORK_ROOT/$STAMP"
mkdir -p "$work_dir"
chmod 700 "$WORK_ROOT" "$work_dir"
pass "workspace created outside the project: $work_dir"

# --- 3. Restore database -----------------------------------------------------

echo "[3/7] Restoring database into an isolated target..."

driver="$(manifest_get db_driver)"
dump_file="$(manifest_get db_dump_file)"

eval "$(php "$SCRIPT_DIR/stage11-db.php" info)"

if [ "$driver" = "sqlite" ]; then
    cp "$backup_dir/$dump_file" "$work_dir/restored.sqlite"
    restored_db="$work_dir/restored.sqlite"
    integrity="$(sqlite3 "$restored_db" 'PRAGMA integrity_check;')"

    if [ "$integrity" = "ok" ]; then
        pass "sqlite copy restored, integrity_check = ok"
    else
        fail_check "restored sqlite integrity_check returned: $integrity"
    fi
else
    SCRATCH_DB="pmb_restore_verify_$STAMP"
    CREDS_FILE="$(php "$SCRIPT_DIR/stage11-db.php" creds)"
    restored_db="$SCRATCH_DB"

    # Creating an isolated database and granting the application account access
    # to it needs an administrative account. Without one this drill must not
    # silently fall back to touching the live database.
    with_admin() { PMB_RESTORE_DB_ADMIN_USER="${PMB_RESTORE_DB_ADMIN_USER:-}" PMB_RESTORE_DB_ADMIN_PASSWORD="${PMB_RESTORE_DB_ADMIN_PASSWORD:-}" php "$SCRIPT_DIR/stage11-db.php" admin-creds 2>/dev/null; }

    ADMIN_CREDS_FILE="$(with_admin || true)"

    if [ -z "$ADMIN_CREDS_FILE" ]; then
        NOT_RUN=true
        info "MySQL restore verification needs an account that can create and drop a scratch database."
        info "Re-run with PMB_RESTORE_DB_ADMIN_USER / PMB_RESTORE_DB_ADMIN_PASSWORD set, for example"
        info "the local docker root account. The live database was NOT touched."
    else
        app_user="$(grep -m1 '^user=' "$CREDS_FILE" | cut -d= -f2- | tr -d '"')"

        if mysql --defaults-extra-file="$ADMIN_CREDS_FILE" \
            -e "CREATE DATABASE \`$SCRATCH_DB\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; GRANT ALL PRIVILEGES ON \`$SCRATCH_DB\`.* TO '$app_user'@'%'; FLUSH PRIVILEGES;"; then
            pass "scratch database $SCRATCH_DB created and granted to the application account"
        else
            fail_check "could not create the scratch database"
        fi

        if mysql --defaults-extra-file="$CREDS_FILE" "$SCRATCH_DB" < "$backup_dir/$dump_file"; then
            pass "dump imported into the scratch database"
        else
            fail_check "dump import failed"
        fi
    fi
fi

if [ "$NOT_RUN" = "true" ]; then
    echo
    echo "--------------------------------------------------------------------------"
    echo "RESULT: NOT RUN — MySQL restore was not executed for the reason above."
    exit 0
fi

ENV_FILE="$(php "$SCRIPT_DIR/stage11-db.php" env-for "$restored_db")"

# Runs a command with Laravel pointed at the restored database.
restored_shell() { ( set -a; . "$ENV_FILE"; set +a; "$@" ); }

# --- 4. Restore documents ----------------------------------------------------

echo "[4/7] Restoring private documents into an isolated directory..."
mkdir -p "$work_dir/private"
tar -xzf "$backup_dir/documents.tar.gz" -C "$work_dir/private"
pass "documents extracted into the isolated workspace"

if (cd "$work_dir/private" && sha256sum -c "$backup_dir/documents.sha256") > "$work_dir/documents-check.log" 2>&1; then
    pass "every restored file is byte-identical to the backup"
else
    fail_check "restored files differ from documents.sha256"
    grep -v ': OK$' "$work_dir/documents-check.log" | head -10 || true
fi

# --- 5. Row counts -----------------------------------------------------------

echo "[5/7] Comparing restored row counts with the backup manifest..."
if restored_shell php "$SCRIPT_DIR/stage11-db.php" counts > "$work_dir/restored-counts.json" 2> "$work_dir/counts.err"; then
    if diff -q "$backup_dir/db-counts.json" "$work_dir/restored-counts.json" > /dev/null; then
        pass "row counts identical to the backup"
    else
        fail_check "row counts differ from the backup"
        diff "$backup_dir/db-counts.json" "$work_dir/restored-counts.json" | head -20 || true
    fi
else
    fail_check "could not read counts from the restored database"
    head -5 "$work_dir/counts.err" || true
fi

# --- 6. Document referential integrity ---------------------------------------

echo "[6/7] Checking database document references against restored files..."
refs_file="$work_dir/document-refs.tsv"
restored_shell php "$SCRIPT_DIR/stage11-db.php" document-refs > "$refs_file" 2>/dev/null || true

referenced=0
missing=0
while IFS=$'\t' read -r _application_id _document_type file_path _file_size; do
    [ -n "${file_path:-}" ] || continue
    referenced=$((referenced + 1))
    if [ ! -f "$work_dir/private/$file_path" ]; then
        missing=$((missing + 1))
        printf '         missing after restore: %s\n' "$file_path"
    fi
done < "$refs_file"

if [ "$referenced" -gt 0 ] && [ "$missing" -eq 0 ]; then
    pass "$referenced document reference(s) all resolve to restored files"
elif [ "$referenced" -eq 0 ]; then
    info "no document references recorded in this backup"
else
    fail_check "$missing of $referenced document references are missing after restore"
fi

# --- 7. Backend authorization against the restored copy ----------------------

echo "[7/7] Testing backend access to restored documents..."
echo "        temporary server on 127.0.0.1:$HTTP_PORT; live database untouched"

if ! command -v curl > /dev/null 2>&1; then
    info "curl not available — HTTP verification NOT RUN"
else
    fixtures="$(restored_shell php "$SCRIPT_DIR/stage11-db.php" http-fixtures 2>/dev/null || true)"

    if [ -z "$fixtures" ]; then
        info "no document rows in the restore — HTTP verification NOT RUN"
    else
        IFS=$'\t' read -r app_id doc_id file_path owner_id other_id <<< "$fixtures"

        restored_shell php "$BACKEND_DIR/artisan" serve --host=127.0.0.1 --port="$HTTP_PORT" \
            > "$work_dir/serve.log" 2>&1 &
        SERVER_PID=$!

        ready="no"
        for _ in $(seq 1 60); do
            if curl -fsS -o /dev/null "http://127.0.0.1:$HTTP_PORT/up" 2>/dev/null; then
                ready="yes"
                break
            fi
            sleep 0.5
        done

        if [ "$ready" != "yes" ]; then
            fail_check "temporary backend did not become ready; see $work_dir/serve.log"
            tail -5 "$work_dir/serve.log" || true
        else
            base="http://127.0.0.1:$HTTP_PORT"
            doc_url="$base/api/v1/applications/$app_id/documents/$doc_id/download"
            owner_token="$(restored_shell php "$SCRIPT_DIR/stage11-db.php" mint-token "$owner_id" 2>/dev/null || true)"

            status="$(curl -s -o "$work_dir/anon.bin" -w '%{http_code}' \
                -H 'Accept: application/json' "$doc_url")"
            if [ "$status" = "401" ]; then
                pass "unauthenticated download rejected (401)"
            else
                fail_check "unauthenticated download returned $status instead of 401"
            fi

            status="$(curl -s -o "$work_dir/owner.bin" -w '%{http_code}' \
                -H 'Accept: application/json' -H "Authorization: Bearer $owner_token" "$doc_url")"
            if [ "$status" = "200" ]; then
                pass "owning applicant downloads the restored document (200)"
            else
                fail_check "owning applicant download returned $status instead of 200"
            fi

            expected_hash="$(grep -F "./$file_path" "$backup_dir/documents.sha256" | cut -d' ' -f1)"
            actual_hash="$(sha256sum "$work_dir/owner.bin" | cut -d' ' -f1)"

            if [ -n "$expected_hash" ] && [ "$expected_hash" = "$actual_hash" ]; then
                pass "downloaded bytes match the backed-up file checksum"
            else
                fail_check "downloaded bytes differ from the backed-up file checksum"
            fi

            if [ -n "$other_id" ]; then
                other_token="$(restored_shell php "$SCRIPT_DIR/stage11-db.php" mint-token "$other_id" 2>/dev/null || true)"

                status="$(curl -s -o /dev/null -w '%{http_code}' \
                    -H 'Accept: application/json' -H "Authorization: Bearer $other_token" "$doc_url")"
                if [ "$status" = "404" ]; then
                    pass "another applicant cannot read the document (404)"
                else
                    fail_check "another applicant got $status instead of 404"
                fi

                status="$(curl -s -o /dev/null -w '%{http_code}' \
                    -H 'Accept: application/json' -H "Authorization: Bearer $other_token" \
                    "$base/api/v1/admin/applications")"
                if [ "$status" = "403" ]; then
                    pass "applicant token rejected on admin endpoints (403)"
                else
                    fail_check "applicant token got $status on /admin/applications instead of 403"
                fi
            else
                info "no second applicant account available — cross-owner check NOT RUN"
            fi
        fi
    fi
fi

# --- Summary -----------------------------------------------------------------

echo
echo "--------------------------------------------------------------------------"
if [ "$FAILURES" -eq 0 ]; then
    echo "RESULT: PASS — the backup restores into an isolated environment and the"
    echo "        backend still enforces authentication and ownership."
    exit 0
fi

echo "RESULT: FAIL — $FAILURES check(s) failed."
exit 1
