import { createAction, props } from '@ngrx/store';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User,
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '../../core/models/user.model';

// Login
export const login = createAction('[Auth] Login', props<{ request: LoginRequest }>());
export const loginSuccess = createAction('[Auth] Login Success', props<{ response: AuthResponse }>());
export const loginFailure = createAction('[Auth] Login Failure', props<{ error: string }>());

// Register
export const register = createAction('[Auth] Register', props<{ request: RegisterRequest }>());
export const registerSuccess = createAction('[Auth] Register Success', props<{ message: string }>());
export const registerFailure = createAction('[Auth] Register Failure', props<{ error: string }>());

// Verify Email
export const verifyEmail = createAction('[Auth] Verify Email', props<{ token: string }>());
export const verifyEmailSuccess = createAction('[Auth] Verify Email Success', props<{ message: string }>());
export const verifyEmailFailure = createAction('[Auth] Verify Email Failure', props<{ error: string }>());

// Load Current User
export const loadCurrentUser = createAction('[Auth] Load Current User');
export const loadCurrentUserSuccess = createAction('[Auth] Load Current User Success', props<{ user: User }>());
export const loadCurrentUserFailure = createAction('[Auth] Load Current User Failure', props<{ error: string }>());

// Refresh Token
export const refreshToken = createAction('[Auth] Refresh Token');
export const refreshTokenSuccess = createAction('[Auth] Refresh Token Success', props<{ response: AuthResponse }>());
export const refreshTokenFailure = createAction('[Auth] Refresh Token Failure');

// Logout
export const logout = createAction('[Auth] Logout');
export const logoutSuccess = createAction('[Auth] Logout Success');

// Forgot Password
export const forgotPassword = createAction('[Auth] Forgot Password', props<{ request: ForgotPasswordRequest }>());
export const forgotPasswordSuccess = createAction('[Auth] Forgot Password Success', props<{ message: string }>());
export const forgotPasswordFailure = createAction('[Auth] Forgot Password Failure', props<{ error: string }>());

// Reset Password
export const resetPassword = createAction('[Auth] Reset Password', props<{ request: ResetPasswordRequest }>());
export const resetPasswordSuccess = createAction('[Auth] Reset Password Success', props<{ message: string }>());
export const resetPasswordFailure = createAction('[Auth] Reset Password Failure', props<{ error: string }>());

// Update Profile
export const updateProfile = createAction(
  '[Auth] Update Profile',
  props<{ request: { firstName: string; lastName: string } }>(),
);
export const updateProfileSuccess = createAction(
  '[Auth] Update Profile Success',
  props<{ user: User }>(),
);
export const updateProfileFailure = createAction(
  '[Auth] Update Profile Failure',
  props<{ error: string }>(),
);

// Clear error
export const clearAuthError = createAction('[Auth] Clear Error');
