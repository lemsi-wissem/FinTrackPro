import { createReducer, on } from '@ngrx/store';
import { initialAuthState } from './auth.state';
import * as AuthActions from './auth.actions';

export const authReducer = createReducer(
  initialAuthState,

  // Login
  on(AuthActions.login, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(AuthActions.loginSuccess, (state, { response }) => ({
    ...state,
    accessToken: response.accessToken,
    loading: false,
    error: null,
  })),
  on(AuthActions.loginFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  // Register
  on(AuthActions.register, (state) => ({
    ...state,
    loading: true,
    error: null,
    registrationSuccess: false,
  })),
  on(AuthActions.registerSuccess, (state) => ({
    ...state,
    loading: false,
    registrationSuccess: true,
  })),
  on(AuthActions.registerFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  // Verify Email
  on(AuthActions.verifyEmail, (state) => ({
    ...state,
    loading: true,
    error: null,
    verificationSuccess: false,
  })),
  on(AuthActions.verifyEmailSuccess, (state) => ({
    ...state,
    loading: false,
    verificationSuccess: true,
  })),
  on(AuthActions.verifyEmailFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  // Load Current User
  on(AuthActions.loadCurrentUser, (state) => ({
    ...state,
    loading: true,
  })),
  on(AuthActions.loadCurrentUserSuccess, (state, { user }) => ({
    ...state,
    user,
    loading: false,
  })),
  on(AuthActions.loadCurrentUserFailure, (state, { error }) => ({
    ...state,
    user: null,
    loading: false,
    error,
  })),

  // Refresh Token
  on(AuthActions.refreshTokenSuccess, (state, { response }) => ({
    ...state,
    accessToken: response.accessToken,
  })),
  on(AuthActions.refreshTokenFailure, (state) => ({
    ...state,
    accessToken: null,
    user: null,
  })),

  // Logout
  on(AuthActions.logout, (state) => ({
    ...state,
    loading: true,
  })),
  on(AuthActions.logoutSuccess, () => ({
    ...initialAuthState,
  })),

  // Forgot Password
  on(AuthActions.forgotPassword, (state) => ({
    ...state,
    loading: true,
    error: null,
    forgotPasswordSuccess: false,
  })),
  on(AuthActions.forgotPasswordSuccess, (state) => ({
    ...state,
    loading: false,
    forgotPasswordSuccess: true,
  })),
  on(AuthActions.forgotPasswordFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  // Reset Password
  on(AuthActions.resetPassword, (state) => ({
    ...state,
    loading: true,
    error: null,
    resetPasswordSuccess: false,
  })),
  on(AuthActions.resetPasswordSuccess, (state) => ({
    ...state,
    loading: false,
    resetPasswordSuccess: true,
  })),
  on(AuthActions.resetPasswordFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  // Clear Error
  on(AuthActions.clearAuthError, (state) => ({
    ...state,
    error: null,
  })),
);
