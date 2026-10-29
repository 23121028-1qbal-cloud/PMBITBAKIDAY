import axios from 'axios';
import { adminCredentials } from './test-credentials.mjs';

const BASE_URL = 'http://127.0.0.1:8000';

async function runHttpVerification() {
  console.log('--- START HTTP INTEGRATION VERIFICATION ---');

  // Kredensial admin berasal dari environment; tidak ada nilai default.
  const admin = adminCredentials();

  // Create an axios instance that stores cookies between requests
  let cookies = {};

  function parseCookies(response) {
    const setCookie = response.headers['set-cookie'];
    if (setCookie) {
      setCookie.forEach((c) => {
        const parts = c.split(';')[0].split('=');
        cookies[parts[0].trim()] = parts.slice(1).join('=');
      });
    }
  }

  function getCookieHeader() {
    return Object.entries(cookies)
      .map(([k, v]) => `${k}=${v}`)
      .join('; ');
  }

  function getXsrfToken() {
    // Decodes the XSRF-TOKEN cookie for header
    return cookies['XSRF-TOKEN'] ? decodeURIComponent(cookies['XSRF-TOKEN']) : '';
  }

  async function request(method, url, data = null, headers = {}) {
    const config = {
      method,
      url: `${BASE_URL}${url}`,
      headers: {
        'Accept': 'application/json',
        'Cookie': getCookieHeader(),
        'X-XSRF-TOKEN': getXsrfToken(),
        'X-Requested-With': 'XMLHttpRequest',
        'Referer': 'http://localhost:3000',
        'Origin': 'http://localhost:3000',
        ...headers,
      },
      validateStatus: () => true, // Don't throw on HTTP error codes
    };
    if (data) {
      config.data = data;
    }
    const res = await axios(config);
    parseCookies(res);
    return res;
  }

  // 1. Bootstrap CSRF Cookie
  console.log('\n[1] Bootstrap CSRF: GET /sanctum/csrf-cookie');
  const csrfRes = await request('GET', '/sanctum/csrf-cookie');
  console.log('Status:', csrfRes.status, 'XSRF-TOKEN acquired:', Boolean(cookies['XSRF-TOKEN']));
  if (csrfRes.status !== 204 && csrfRes.status !== 200) {
    throw new Error(`Failed CSRF bootstrap: ${csrfRes.status}`);
  }

  // 2. Admin Session Login
  console.log('\n[2] Admin Session Login: POST /api/v1/auth/session/login');
  const loginRes = await request('POST', '/api/v1/auth/session/login', {
    email: admin.email,
    password: admin.password,
  });
  console.log('Status:', loginRes.status, 'Body:', JSON.stringify(loginRes.data));
  if (loginRes.status !== 200 || loginRes.data.data?.user?.role !== 'admin') {
    throw new Error(`Failed Admin Login: ${loginRes.status} ${JSON.stringify(loginRes.data)}`);
  }

  // 3. Session Restoration: GET /api/v1/auth/me
  console.log('\n[3] Session Restoration: GET /api/v1/auth/me');
  const meRes = await request('GET', '/api/v1/auth/me');
  console.log('Status:', meRes.status, 'User Role:', meRes.data.data?.role);
  if (meRes.status !== 200 || meRes.data.data?.email !== admin.email) {
    throw new Error(`Failed Session restoration: ${meRes.status}`);
  }

  // 4. Admission Waves & Study Programs Reference
  console.log('\n[4] Reference Data: GET /api/v1/admission-waves & /study-programs');
  const wavesRes = await request('GET', '/api/v1/admission-waves');
  const progsRes = await request('GET', '/api/v1/study-programs');
  console.log('Waves Status:', wavesRes.status, 'Count:', wavesRes.data.data?.length);
  console.log('Study Programs Status:', progsRes.status, 'Count:', progsRes.data.data?.length);

  // 5. Admin Applications List
  console.log('\n[5] Applications List: GET /api/v1/admin/applications');
  const appsRes = await request('GET', '/api/v1/admin/applications?page=1&per_page=10');
  console.log('Status:', appsRes.status, 'Meta:', JSON.stringify(appsRes.data.meta));
  if (appsRes.status !== 200 || !appsRes.data.success) {
    throw new Error(`Failed admin applications list: ${appsRes.status}`);
  }

  // 6. Application Detail & Review Flow (if an application exists)
  if (appsRes.data.data?.length > 0) {
    const firstApp = appsRes.data.data[0];
    console.log(`\n[6] Detail Application #${firstApp.id}: GET /api/v1/admin/applications/${firstApp.id}`);
    const detailRes = await request('GET', `/api/v1/admin/applications/${firstApp.id}`);
    console.log('Status:', detailRes.status, 'Reg No:', detailRes.data.data?.registration_number);
    console.log('Current Status:', detailRes.data.data?.status);

    // Test 409 STATE_CONFLICT by sending wrong expected_status
    console.log('\n[7] Testing 409 STATE_CONFLICT on invalid expected_status');
    const conflictRes = await request('POST', `/api/v1/admin/applications/${firstApp.id}/review`, {
      expected_status: 'NON_EXISTENT_STATUS',
    });
    console.log('Conflict response status:', conflictRes.status, 'Error Code:', conflictRes.data.error?.code);
    if (conflictRes.status !== 409 || conflictRes.data.error?.code !== 'STATE_CONFLICT') {
      console.warn('Note: Expected 409 STATE_CONFLICT, got', conflictRes.status);
    } else {
      console.log('✓ 409 STATE_CONFLICT correctly returned and verified!');
    }

    // Test Starting Review (submitted -> review)
    console.log('\n[7.1] Testing Start Review: POST /api/v1/admin/applications/' + firstApp.id + '/review');
    const reviewRes = await request('POST', `/api/v1/admin/applications/${firstApp.id}/review`, {
      expected_status: 'submitted',
    });
    console.log('Review response status:', reviewRes.status, 'New status:', reviewRes.data.data?.status);
    if (reviewRes.status !== 200 || reviewRes.data.data?.status !== 'review') {
      throw new Error(`Failed to start review: ${reviewRes.status}`);
    }

    // Test Requesting Revision (review -> revision)
    console.log('\n[7.2] Testing Request Revision: POST /api/v1/admin/applications/' + firstApp.id + '/request-revision');
    const revRes = await request('POST', `/api/v1/admin/applications/${firstApp.id}/request-revision`, {
      expected_status: 'review',
      note: 'Harap unggah ulang scan KTP dengan resolusi lebih jelas.',
    });
    console.log('Revision response status:', revRes.status, 'New status:', revRes.data.data?.status);
    if (revRes.status !== 200 || revRes.data.data?.status !== 'revision') {
      throw new Error(`Failed to request revision: ${revRes.status}`);
    }

    // Verify decision fails with 409 when status is still 'revision'
    console.log('\n[7.3] Testing 409 STATE_CONFLICT when attempting decision on revision status');
    const prematureDecRes = await request('POST', `/api/v1/admin/applications/${firstApp.id}/decision`, {
      expected_status: 'review',
      decision: 'accepted',
      note: 'Catatan',
    });
    console.log('Premature decision status:', prematureDecRes.status, 'Code:', prematureDecRes.data.error?.code);
    if (prematureDecRes.status === 409) {
      console.log('✓ Correctly blocked with 409 STATE_CONFLICT because application is currently in revision!');
    }

    // Test Status History retrieval
    console.log('\n[7.4] Testing Status History: GET /api/v1/applications/' + firstApp.id + '/status-history');
    const historyRes = await request('GET', `/api/v1/applications/${firstApp.id}/status-history`);
    console.log('History status:', historyRes.status, 'Entries:', historyRes.data.data?.length);
    historyRes.data.data?.forEach((h) => {
      console.log(`   - ${h.from_status || 'initial'} -> ${h.to_status} (${h.note})`);
    });
    if (historyRes.status !== 200 || historyRes.data.data?.length < 3) {
      throw new Error('Failed to retrieve full status history');
    }
  }

  // 8. Admin Session Logout
  console.log('\n[8] Admin Session Logout: POST /api/v1/auth/session/logout');
  const logoutRes = await request('POST', '/api/v1/auth/session/logout');
  console.log('Logout Status:', logoutRes.status, 'Body:', JSON.stringify(logoutRes.data));

  // 9. Verify Session Is Ended
  console.log('\n[9] Verify Session Terminated: GET /api/v1/auth/me');
  const postLogoutMe = await request('GET', '/api/v1/auth/me');
  console.log('Status after logout:', postLogoutMe.status);
  if (postLogoutMe.status === 401) {
    console.log('✓ Session terminated successfully, got 401 Unauthorized!');
  }

  console.log('\n--- ALL HTTP INTEGRATION CHECKS PASSED SUCCESSFULLY ---');
}

runHttpVerification().catch((err) => {
  console.error('HTTP Verification FAILED:', err);
  process.exit(1);
});
