/**
 * Kredensial untuk skrip integrasi manual di folder ini.
 *
 * Skrip-skrip ini login ke backend lokal yang sungguhan, jadi kredensialnya
 * TIDAK boleh ditulis di repository. Semua nilai dibaca dari environment dan
 * tidak ada nilai default: bila variabel belum diisi, skrip berhenti dengan
 * pesan yang jelas alih-alih diam-diam memakai kata sandi yang pernah
 * di-commit.
 *
 * Variabel yang dibaca:
 *   PMB_TEST_ADMIN_EMAIL      / PMB_TEST_ADMIN_PASSWORD
 *   PMB_TEST_PANITIA_EMAIL    / PMB_TEST_PANITIA_PASSWORD
 *   PMB_TEST_PIMPINAN_EMAIL   / PMB_TEST_PIMPINAN_PASSWORD
 *
 * Contoh (nilai tidak boleh disimpan ke file yang dilacak Git):
 *   PMB_TEST_ADMIN_EMAIL=... PMB_TEST_ADMIN_PASSWORD=... \
 *     node test/http-integration-verify.mjs
 */

function required(role, name) {
  const value = process.env[name];
  if (!value || !value.trim()) {
    throw new Error(
      `Konfigurasi kredensial test tidak lengkap: ${name} belum diisi.\n` +
        `Skrip ini membutuhkan akun ${role} yang benar-benar ada di database lokal.\n` +
        `Set variabel environment-nya lebih dulu (lihat komentar di test/test-credentials.mjs).`,
    );
  }
  return value;
}

function credentialsFor(role) {
  const prefix = `PMB_TEST_${role.toUpperCase()}`;
  return {
    email: required(role, `${prefix}_EMAIL`).trim(),
    password: required(role, `${prefix}_PASSWORD`),
  };
}

/** Akun admin (role `admin`). */
export function adminCredentials() {
  return credentialsFor('admin');
}

/** Akun panitia (role `panitia`). */
export function panitiaCredentials() {
  return credentialsFor('panitia');
}

/** Akun pimpinan (role `pimpinan`). */
export function pimpinanCredentials() {
  return credentialsFor('pimpinan');
}

/**
 * Kata sandi acak untuk akun yang dibuat oleh skrip itu sendiri. Akun ini
 * hanya hidup selama pengujian, jadi tidak ada nilai tetap yang perlu
 * disimpan — dan tidak ada kata sandi yang bisa dipakai ulang dari repo.
 */
export function generatedPassword() {
  const bytes = new Uint8Array(18);
  crypto.getRandomValues(bytes);
  const random = Array.from(bytes, (b) => b.toString(16).padStart(2, '0')).join('');
  return `Pmb!${random}9a`;
}
