<?php

namespace App\Exceptions;

use Exception;

/**
 * Raised when authentication succeeds but the account is disabled.
 *
 * Mapped to HTTP 403 with error code ACCOUNT_INACTIVE by ApiErrorResponse so
 * the client never has to parse a message string.
 */
class AccountInactiveException extends Exception
{
    //
}
