<?php

use App\Support\ApiErrorResponse;
use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Http\Request;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        api: __DIR__.'/../routes/api.php',
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
        apiPrefix: 'api/v1',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        // Enables Laravel Sanctum's first-party stateful flow so the admin
        // browser can authenticate with session cookies on /api/v1 routes.
        $middleware->statefulApi();

        // Laravel defaults this to route('login'), which does not exist in this
        // API-only app. Without a plain path the Authenticate middleware throws
        // RouteNotFoundException instead of AuthenticationException, turning a
        // documented 401 into an unhandled 500.
        $middleware->redirectGuestsTo('/login');

        $middleware->alias([
            'role' => \App\Http\Middleware\EnsureRole::class,
        ]);
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        // API requests always receive the documented JSON envelope; regular
        // browser requests outside the API keep Laravel's default rendering.
        $exceptions->shouldRenderJsonWhen(
            fn (Request $request, Throwable $e) => $request->is('api/*') || $request->expectsJson(),
        );

        $exceptions->render(function (Throwable $e, Request $request) {
            if (! $request->is('api/*') && ! $request->expectsJson()) {
                return null;
            }

            return ApiErrorResponse::fromThrowable($e);
        });
    })->create();
