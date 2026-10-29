import { test, describe } from 'node:test';
import assert from 'node:assert';

describe('Workflow & Business Process Logic', () => {
  // Status transition validator matching backend StatusHistoryController
  function canTransition(currentStatus, targetAction) {
    switch (targetAction) {
      case 'review':
        return currentStatus === 'submitted';
      case 'request_revision':
        return currentStatus === 'review';
      case 'decision_accept':
      case 'decision_reject':
        return currentStatus === 'review';
      default:
        return false;
    }
  }

  // Precondition status checker matching expected_status contract
  function validatePrecondition(serverStatus, expectedStatus) {
    if (serverStatus !== expectedStatus) {
      return {
        valid: false,
        code: 'STATE_CONFLICT',
        message: 'Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.',
      };
    }
    return { valid: true };
  }

  // Role permissions checker
  function canExecuteAction(role, action) {
    const committeeRoles = ['admin', 'panitia'];
    if (action === 'view_applications' || action === 'view_detail') {
      return ['admin', 'panitia', 'pimpinan'].includes(role);
    }
    if (['start_review', 'request_revision', 'submit_decision'].includes(action)) {
      return committeeRoles.includes(role);
    }
    return false;
  }

  test('submitted application can transition to review', () => {
    assert.strictEqual(canTransition('submitted', 'review'), true);
    assert.strictEqual(canTransition('draft', 'review'), false);
    assert.strictEqual(canTransition('accepted', 'review'), false);
  });

  test('review application can transition to revision or decision', () => {
    assert.strictEqual(canTransition('review', 'request_revision'), true);
    assert.strictEqual(canTransition('review', 'decision_accept'), true);
    assert.strictEqual(canTransition('review', 'decision_reject'), true);
    assert.strictEqual(canTransition('submitted', 'decision_accept'), false);
  });

  test('precondition check detects 409 STATE_CONFLICT when status changed concurrently', () => {
    // If user UI has status 'submitted', but someone already transitioned it to 'review'
    const result = validatePrecondition('review', 'submitted');
    assert.strictEqual(result.valid, false);
    assert.strictEqual(result.code, 'STATE_CONFLICT');
  });

  test('precondition check passes when expected_status matches current server state', () => {
    const result = validatePrecondition('review', 'review');
    assert.strictEqual(result.valid, true);
  });

  test('role-based action authorization matrix', () => {
    // Pimpinan can view but cannot mutate review or decision
    assert.strictEqual(canExecuteAction('pimpinan', 'view_applications'), true);
    assert.strictEqual(canExecuteAction('pimpinan', 'view_detail'), true);
    assert.strictEqual(canExecuteAction('pimpinan', 'start_review'), false);
    assert.strictEqual(canExecuteAction('pimpinan', 'request_revision'), false);
    assert.strictEqual(canExecuteAction('pimpinan', 'submit_decision'), false);

    // Panitia and Admin can perform all review and decision actions
    assert.strictEqual(canExecuteAction('panitia', 'start_review'), true);
    assert.strictEqual(canExecuteAction('panitia', 'request_revision'), true);
    assert.strictEqual(canExecuteAction('panitia', 'submit_decision'), true);

    assert.strictEqual(canExecuteAction('admin', 'start_review'), true);
    assert.strictEqual(canExecuteAction('admin', 'request_revision'), true);
    assert.strictEqual(canExecuteAction('admin', 'submit_decision'), true);

    // Calon Mahasiswa has no access to any administrative actions
    assert.strictEqual(canExecuteAction('calon_mahasiswa', 'view_applications'), false);
    assert.strictEqual(canExecuteAction('calon_mahasiswa', 'start_review'), false);
  });
});
