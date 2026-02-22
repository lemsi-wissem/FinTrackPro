import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of, throwError } from 'rxjs';
import { Action } from '@ngrx/store';
import { Router } from '@angular/router';
import { AuthEffects } from './auth.effects';
import { AuthService } from '../../core/services/auth.service';
import { TokenService } from '../../core/services/token.service';
import { UserService } from '../../core/services/user.service';
import * as AuthActions from './auth.actions';
import { User, AuthResponse } from '../../core/models/user.model';

describe('AuthEffects', () => {
  let actions$: Observable<Action>;
  let effects: AuthEffects;
  let authService: jasmine.SpyObj<AuthService>;
  let userService: jasmine.SpyObj<UserService>;
  let tokenService: jasmine.SpyObj<TokenService>;
  let router: jasmine.SpyObj<Router>;

  const mockUser: User = {
    id: 'user-1',
    email: 'alice@example.com',
    firstName: 'Alice',
    lastName: 'Smith',
    role: 'ROLE_USER',
    verified: true,
    createdAt: '2026-01-01T00:00:00Z',
  };

  const mockAuthResponse: AuthResponse = {
    accessToken: 'jwt-abc',
    tokenType: 'Bearer',
  };

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', [
      'login',
      'register',
      'verifyEmail',
      'getCurrentUser',
      'logout',
      'refreshToken',
      'forgotPassword',
      'resetPassword',
    ]);
    const userSpy = jasmine.createSpyObj('UserService', ['updateProfile', 'changePassword']);
    const tokenSpy = jasmine.createSpyObj('TokenService', [
      'setAccessToken',
      'removeAccessToken',
    ]);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthEffects,
        provideMockActions(() => actions$),
        { provide: AuthService, useValue: authSpy },
        { provide: UserService, useValue: userSpy },
        { provide: TokenService, useValue: tokenSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });

    effects = TestBed.inject(AuthEffects);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    tokenService = TestBed.inject(TokenService) as jasmine.SpyObj<TokenService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  describe('login$', () => {
    it('should dispatch loginSuccess on successful login', (done) => {
      actions$ = of(AuthActions.login({ request: { email: 'a@b.com', password: 'pass' } }));
      authService.login.and.returnValue(of(mockAuthResponse));

      effects.login$.subscribe((action) => {
        expect(action).toEqual(AuthActions.loginSuccess({ response: mockAuthResponse }));
        done();
      });
    });

    it('should dispatch loginFailure on error', (done) => {
      actions$ = of(AuthActions.login({ request: { email: 'a@b.com', password: 'wrong' } }));
      authService.login.and.returnValue(
        throwError(() => ({ error: { message: 'Invalid credentials' } })),
      );

      effects.login$.subscribe((action) => {
        expect(action).toEqual(AuthActions.loginFailure({ error: 'Invalid credentials' }));
        done();
      });
    });
  });

  describe('loginSuccess$', () => {
    it('should store token and dispatch loadCurrentUser', (done) => {
      actions$ = of(AuthActions.loginSuccess({ response: mockAuthResponse }));

      effects.loginSuccess$.subscribe((action) => {
        expect(tokenService.setAccessToken).toHaveBeenCalledWith('jwt-abc');
        expect(action).toEqual(AuthActions.loadCurrentUser());
        done();
      });
    });
  });

  describe('loadCurrentUser$', () => {
    it('should dispatch loadCurrentUserSuccess on success', (done) => {
      actions$ = of(AuthActions.loadCurrentUser());
      authService.getCurrentUser.and.returnValue(of(mockUser));

      effects.loadCurrentUser$.subscribe((action) => {
        expect(action).toEqual(AuthActions.loadCurrentUserSuccess({ user: mockUser }));
        done();
      });
    });

    it('should dispatch loadCurrentUserFailure on error', (done) => {
      actions$ = of(AuthActions.loadCurrentUser());
      authService.getCurrentUser.and.returnValue(
        throwError(() => ({ error: { message: 'Unauthorized' } })),
      );

      effects.loadCurrentUser$.subscribe((action) => {
        expect(action).toEqual(
          AuthActions.loadCurrentUserFailure({ error: 'Unauthorized' }),
        );
        done();
      });
    });
  });

  describe('logout$', () => {
    it('should dispatch logoutSuccess after logout', (done) => {
      actions$ = of(AuthActions.logout());
      authService.logout.and.returnValue(of(void 0 as any));

      effects.logout$.subscribe((action) => {
        expect(action).toEqual(AuthActions.logoutSuccess());
        done();
      });
    });

    it('should dispatch logoutSuccess even on logout error', (done) => {
      actions$ = of(AuthActions.logout());
      authService.logout.and.returnValue(throwError(() => new Error('Network error')));

      effects.logout$.subscribe((action) => {
        expect(action).toEqual(AuthActions.logoutSuccess());
        done();
      });
    });
  });

  describe('updateProfile$', () => {
    it('should dispatch updateProfileSuccess on success', (done) => {
      const updatedUser: User = { ...mockUser, firstName: 'Bob', lastName: 'Jones' };
      actions$ = of(
        AuthActions.updateProfile({ request: { firstName: 'Bob', lastName: 'Jones' } }),
      );
      userService.updateProfile.and.returnValue(of(updatedUser));

      effects.updateProfile$.subscribe((action) => {
        expect(action).toEqual(AuthActions.updateProfileSuccess({ user: updatedUser }));
        done();
      });
    });

    it('should dispatch updateProfileFailure on error', (done) => {
      actions$ = of(
        AuthActions.updateProfile({ request: { firstName: 'Bob', lastName: 'Jones' } }),
      );
      userService.updateProfile.and.returnValue(
        throwError(() => ({ error: { message: 'Update failed' } })),
      );

      effects.updateProfile$.subscribe((action) => {
        expect(action).toEqual(AuthActions.updateProfileFailure({ error: 'Update failed' }));
        done();
      });
    });
  });

  describe('forgotPassword$', () => {
    it('should dispatch forgotPasswordSuccess on success', (done) => {
      actions$ = of(AuthActions.forgotPassword({ request: { email: 'a@b.com' } }));
      authService.forgotPassword.and.returnValue(of({ message: 'Email sent' }));

      effects.forgotPassword$.subscribe((action) => {
        expect(action).toEqual(AuthActions.forgotPasswordSuccess({ message: 'Email sent' }));
        done();
      });
    });
  });
});
