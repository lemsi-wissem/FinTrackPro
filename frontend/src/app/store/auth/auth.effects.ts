import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import { TokenService } from '../../core/services/token.service';
import * as AuthActions from './auth.actions';

@Injectable()
export class AuthEffects {
  constructor(
    private actions$: Actions,
    private authService: AuthService,
    private tokenService: TokenService,
    private router: Router,
  ) {}

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.login),
      exhaustMap(({ request }) =>
        this.authService.login(request).pipe(
          map((response) => AuthActions.loginSuccess({ response })),
          catchError((err) =>
            of(AuthActions.loginFailure({ error: err.error?.message || 'Login failed' })),
          ),
        ),
      ),
    ),
  );

  loginSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginSuccess),
      tap(({ response }) => this.tokenService.setAccessToken(response.accessToken)),
      map(() => AuthActions.loadCurrentUser()),
    ),
  );

  register$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.register),
      exhaustMap(({ request }) =>
        this.authService.register(request).pipe(
          map((res) => AuthActions.registerSuccess({ message: res.message })),
          catchError((err) =>
            of(AuthActions.registerFailure({ error: err.error?.message || 'Registration failed' })),
          ),
        ),
      ),
    ),
  );

  verifyEmail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.verifyEmail),
      exhaustMap(({ token }) =>
        this.authService.verifyEmail(token).pipe(
          map((res) => AuthActions.verifyEmailSuccess({ message: res.message })),
          catchError((err) =>
            of(AuthActions.verifyEmailFailure({ error: err.error?.message || 'Verification failed' })),
          ),
        ),
      ),
    ),
  );

  loadCurrentUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loadCurrentUser),
      exhaustMap(() =>
        this.authService.getCurrentUser().pipe(
          map((user) => AuthActions.loadCurrentUserSuccess({ user })),
          catchError((err) =>
            of(AuthActions.loadCurrentUserFailure({ error: err.error?.message || 'Failed to load user' })),
          ),
        ),
      ),
    ),
  );

  loadCurrentUserSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.loadCurrentUserSuccess),
        tap(() => this.router.navigate(['/dashboard'])),
      ),
    { dispatch: false },
  );

  logout$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.logout),
      exhaustMap(() =>
        this.authService.logout().pipe(
          map(() => AuthActions.logoutSuccess()),
          catchError(() => of(AuthActions.logoutSuccess())),
        ),
      ),
    ),
  );

  logoutSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.logoutSuccess),
        tap(() => {
          this.tokenService.removeAccessToken();
          this.router.navigate(['/auth/login']);
        }),
      ),
    { dispatch: false },
  );

  refreshToken$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.refreshToken),
      exhaustMap(() =>
        this.authService.refreshToken().pipe(
          map((response) => AuthActions.refreshTokenSuccess({ response })),
          catchError(() => of(AuthActions.refreshTokenFailure())),
        ),
      ),
    ),
  );

  refreshTokenSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.refreshTokenSuccess),
        tap(({ response }) => this.tokenService.setAccessToken(response.accessToken)),
      ),
    { dispatch: false },
  );

  refreshTokenFailure$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.refreshTokenFailure),
        tap(() => {
          this.tokenService.removeAccessToken();
          this.router.navigate(['/auth/login']);
        }),
      ),
    { dispatch: false },
  );

  forgotPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.forgotPassword),
      exhaustMap(({ request }) =>
        this.authService.forgotPassword(request).pipe(
          map((res) => AuthActions.forgotPasswordSuccess({ message: res.message })),
          catchError((err) =>
            of(AuthActions.forgotPasswordFailure({ error: err.error?.message || 'Request failed' })),
          ),
        ),
      ),
    ),
  );

  resetPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.resetPassword),
      exhaustMap(({ request }) =>
        this.authService.resetPassword(request).pipe(
          map((res) => AuthActions.resetPasswordSuccess({ message: res.message })),
          catchError((err) =>
            of(AuthActions.resetPasswordFailure({ error: err.error?.message || 'Reset failed' })),
          ),
        ),
      ),
    ),
  );
}
