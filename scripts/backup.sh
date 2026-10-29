#!/usr/bin/env bash
#
# PMB ITB Akiday — Tahap 11 backup.
#
# Creates one timestamped backup containing:
#   * a consistent database dump (SQLite snapshot or MySQL/MariaDB dump),
#   * an archive of the private document storage that the backend really uses,
#   * a manifest (time, source, versions, counts, status),
#   * SHA256SUMS for every artifact.
#
# Usage:
#   scripts/backup.sh
#   PMB_BACKUP_ROOT=/media/external/pmb-backups scripts/backup.sh
#
# The backup root defaults to ../pmb_backups next to the project, i.e. outside
# the source tree. Nothing is deleted from the source, and no credential is
# ever written to a log, the manifest, or a command line.

set -Eeuo pipefail
umask 077

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend-api"
BACKUP_ROOT="${PMB_BACKUP_ROOT:-$PROJECT_ROOT/../pmb_backups}"
BACKUP_ID="$(date -u +%Y%m%dT%H%M%SZ)"
TARGET="$BACKUP_ROOT/$BACKUP_ID"

STATUS="incomplete"

fail() {
    echo "ERROR: $*" >&2
    STATUS="failed"
    exit 1
}

write_manifest() {
    local manifest="$TARGET/manifest.txt"
    {
        echo "backup_id=$BACKUP_ID"
        echo "created_at_utc=$(date -u +%Y-%m-%dT%H:%M:%SZ)"
        echo "status=$STATUS"
        echo "project=PMB ITB Akiday"
        echo "host=$(hostname)"
        echo "app_env=${PMB_APP_ENV:-unknown}"
        echo "framework_version=${PMB_FRAMEWORK_VERSION:-unknown}"
        echo "php_version=$(php -r 'echo PHP_VERSION;')"
        echo "db_connection=${PMB_DB_CONNECTION:-unknown}"
        echo "db_driver=${PMB_DB_DRIVER:-unknown}"
        echo "db_name=$(basename "${PMB_DB_NAME:-unknown}")"
        echo "db_dump_file=${DB_FILE:-none}"
        echo "db_dump_bytes=${DB_BYTES:-0}"
        echo "db_dump_sha256=${DB_SHA:-none}"
        echo "db_table_count=${DB_TABLE_COUNT:-0}"
        echo "documents_source=${DOCS_SOURCE_REL:-unknown}"
        echo "documents_archive=documents.tar.gz"
        echo "documents_archive_bytes=${DOCS_ARCHIVE_BYTES:-0}"
        echo "documents_archive_sha256=${DOCS_ARCHIVE_SHA:-none}"
        echo "documents_file_count=${DOCS_FILE_COUNT:-0}"
        echo "documents_total_bytes=${DOCS_TOTAL_BYTES:-0}"
        echo "documents_changed_during_backup=${DOCS_CHANGED:-unknown}"
        echo "documents_referenced=${DOCS_REFERENCED:-0}"
        echo "documents_referenced_missing=${DOCS_MISSING:-0}"
        echo "documents_archive_matches_source=${DOCS_ARCHIVE_MATCHES:-unknown}"
        echo "filesystem_enforces_permissions=${PERMISSIONS_ENFORCED:-unknown}"
        echo "counts_file=db-counts.json"
        echo "documents_checksums_file=documents.sha256"
        echo "checksums_file=SHA256SUMS"
        echo "restore_note=Use scripts/restore-verify.sh; never restore over live data."
    } > "$manifest"
    chmod 600 "$manifest"
}

cleanup_on_error() {
    local exit_code=$?
    if [ "$exit_code" -ne 0 ] && [ -d "$TARGET" ]; then
        STATUS="failed"
        write_manifest || true
        echo "Backup FAILED. Partial output kept for inspection at: $TARGET" >&2
    fi
}
trap cleanup_on_error EXIT

# --- Preflight ---------------------------------------------------------------

[ -d "$BACKEND_DIR" ] || fail "backend-api directory not found"

# Reads the connection/file paths Laravel itself resolves; no secret is printed.
eval "$(php "$SCRIPT_DIR/stage11-db.php" info)"

[ -n "${PMB_PRIVATE_STORAGE:-}" ] || fail "unable to determine private storage path"
[ -d "$PMB_PRIVATE_STORAGE" ] || fail "private storage path does not exist: $PMB_PRIVATE_STORAGE"

if [ -e "$TARGET" ]; then
    fail "backup target already exists: $TARGET"
fi

mkdir -p "$BACKUP_ROOT"
chmod 700 "$BACKUP_ROOT"
mkdir -p "$TARGET"
chmod 700 "$TARGET"

# Some filesystems (NTFS/FAT mounts, for example) ignore chmod entirely. Say so
# instead of implying the archive is protected when it is not.
PERMISSIONS_ENFORCED="yes"
if [ "$(stat -c '%a' "$TARGET")" != "700" ]; then
    PERMISSIONS_ENFORCED="no"
    echo "WARNING: this filesystem does not enforce file modes (backup dir reads as" >&2
    echo "         $(stat -c '%a' "$TARGET")); the archive is only as protected as the drive." >&2
    echo "         Prefer a backup location on a filesystem that enforces permissions." >&2
    echo >&2
fi

echo "Backup id : $BACKUP_ID"
echo "Driver    : $PMB_DB_DRIVER"
echo "Documents : $PMB_PRIVATE_STORAGE"
echo

# --- Database ----------------------------------------------------------------

case "$PMB_DB_DRIVER" in
    sqlite)
        DB_FILE="database.sqlite"
        ;;
    mysql|mariadb)
        DB_FILE="database.sql"
        ;;
    *)
        fail "unsupported database driver: $PMB_DB_DRIVER"
        ;;
esac

echo "[1/5] Dumping database ($PMB_DB_DRIVER)..."
php "$SCRIPT_DIR/stage11-db.php" dump "$TARGET/$DB_FILE" > /dev/null

[ -s "$TARGET/$DB_FILE" ] || fail "database dump is missing or empty"

DB_BYTES="$(stat -c '%s' "$TARGET/$DB_FILE")"
DB_SHA="$(sha256sum "$TARGET/$DB_FILE" | cut -d' ' -f1)"

if [ "$PMB_DB_DRIVER" = "sqlite" ]; then
    integrity="$(sqlite3 "$TARGET/$DB_FILE" 'PRAGMA integrity_check;')"
    [ "$integrity" = "ok" ] || fail "dump integrity_check failed: $integrity"
    DB_TABLE_COUNT="$(sqlite3 "$TARGET/$DB_FILE" "select count(*) from sqlite_master where type='table' and name not like 'sqlite_%';")"
else
    # The dump must contain table definitions and end with the dump marker, so a
    # truncated file is caught here instead of at restore time.
    grep -q 'CREATE TABLE' "$TARGET/$DB_FILE" || fail "dump contains no CREATE TABLE statement"
    grep -q 'Dump completed' "$TARGET/$DB_FILE" || fail "dump does not end with the completion marker"
    DB_TABLE_COUNT="$(grep -c 'CREATE TABLE' "$TARGET/$DB_FILE" || true)"
fi

[ "${DB_TABLE_COUNT:-0}" -gt 0 ] || fail "dump contains no tables"
echo "      ok: $DB_FILE ($DB_BYTES bytes, $DB_TABLE_COUNT tables)"

php "$SCRIPT_DIR/stage11-db.php" counts > "$TARGET/db-counts.json"
chmod 600 "$TARGET/db-counts.json"

# --- Documents ---------------------------------------------------------------

echo "[2/5] Archiving private documents..."

fingerprint() {
    (cd "$PMB_PRIVATE_STORAGE" && find . -type f -printf '%P\t%s\t%T@\n' | LC_ALL=C sort) \
        | sha256sum | cut -d' ' -f1
}

DOCS_SOURCE_REL="${PMB_PRIVATE_STORAGE#"$PROJECT_ROOT"/}"
BEFORE_FINGERPRINT="$(fingerprint)"
DOCS_FILE_COUNT="$(find "$PMB_PRIVATE_STORAGE" -type f | wc -l | tr -d ' ')"
DOCS_TOTAL_BYTES="$(find "$PMB_PRIVATE_STORAGE" -type f -printf '%s\n' | awk '{total += $1} END {print total + 0}')"

# -C + "." keeps the stored paths identical to the file_path values in the
# database (e.g. documents/2/doc_ktp_xxx.pdf).
tar -czf "$TARGET/documents.tar.gz" -C "$PMB_PRIVATE_STORAGE" .

AFTER_FINGERPRINT="$(fingerprint)"

if [ "$BEFORE_FINGERPRINT" = "$AFTER_FINGERPRINT" ]; then
    DOCS_CHANGED="no"
else
    DOCS_CHANGED="yes"
    echo "      WARNING: documents changed while the archive was written; see manifest" >&2
fi

[ -s "$TARGET/documents.tar.gz" ] || fail "documents archive is missing or empty"
gzip -t "$TARGET/documents.tar.gz" || fail "documents archive is not a valid gzip file"

ARCHIVE_ENTRIES="$(tar -tzf "$TARGET/documents.tar.gz" | grep -vc '/$' || true)"
[ "$ARCHIVE_ENTRIES" -eq "$DOCS_FILE_COUNT" ] \
    || fail "archive holds $ARCHIVE_ENTRIES files but the source holds $DOCS_FILE_COUNT"

DOCS_ARCHIVE_BYTES="$(stat -c '%s' "$TARGET/documents.tar.gz")"
DOCS_ARCHIVE_SHA="$(sha256sum "$TARGET/documents.tar.gz" | cut -d' ' -f1)"
DOCS_ARCHIVE_MATCHES="yes"

# Per-file checksums taken from the live storage, so a restore can prove that
# every restored file is byte-identical rather than merely present.
(cd "$PMB_PRIVATE_STORAGE" && find . -type f -print0 | LC_ALL=C sort -z | xargs -0 -r sha256sum) \
    > "$TARGET/documents.sha256"
chmod 600 "$TARGET/documents.sha256"

echo "      ok: documents.tar.gz ($DOCS_FILE_COUNT files, $DOCS_TOTAL_BYTES bytes)"

# --- Referenced documents ----------------------------------------------------

echo "[3/5] Checking database references against stored files..."

DOCS_REFERENCED=0
DOCS_MISSING=0

while IFS=$'\t' read -r _application_id _document_type file_path _file_size; do
    [ -n "${file_path:-}" ] || continue
    DOCS_REFERENCED=$((DOCS_REFERENCED + 1))
    if [ ! -f "$PMB_PRIVATE_STORAGE/$file_path" ]; then
        DOCS_MISSING=$((DOCS_MISSING + 1))
        echo "      MISSING: $file_path" >&2
    fi
done < <(php "$SCRIPT_DIR/stage11-db.php" document-refs || true)

echo "      ok: $DOCS_REFERENCED referenced, $DOCS_MISSING missing on disk"

# --- Manifest and checksums --------------------------------------------------

echo "[4/5] Writing manifest and checksums..."

STATUS="complete"
write_manifest

(cd "$TARGET" && sha256sum "$DB_FILE" documents.tar.gz documents.sha256 db-counts.json > SHA256SUMS)
chmod 600 "$TARGET/SHA256SUMS"

# --- Final verification ------------------------------------------------------

echo "[5/5] Verifying artifacts..."

[ -s "$TARGET/manifest.txt" ] || fail "manifest was not written"
(cd "$TARGET" && sha256sum -c --quiet SHA256SUMS) || fail "checksum verification failed"

echo
echo "Backup complete."
echo "  location : $TARGET"
echo "  database : $DB_FILE ($DB_BYTES bytes)"
echo "  documents: documents.tar.gz ($DOCS_FILE_COUNT files, $DOCS_TOTAL_BYTES bytes)"
echo "  status   : $STATUS"
echo
echo "Next: verify the backup with scripts/restore-verify.sh \"$TARGET\""

trap - EXIT
