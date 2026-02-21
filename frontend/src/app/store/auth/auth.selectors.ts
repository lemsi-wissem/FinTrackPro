import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AuthState } from './auth.state';

export const selectAuthState = createFeatureSelector<AuthState>('auth');

export const selectCurrentUser = createSelector(selectAuthState, (state) => state.user);
export const selectAccessToken = createSelector(selectAuthState, (state) => state.accessToken);
export const selectIsAuthenticated = createSelector(selectAuthState, (state) => !!state.accessToken);
export const selectAuthLoading = createSelector(selectAuthState, (state) => state.loading);
export const selectAuthError = createSelector(selectAuthState, (state) => state.error);
export const selectRegistrationSuccess = createSelector(selectAuthState, (state) => state.registrationSuccess);
export const selectVerificationSuccess = createSelector(selectAuthState, (state) => state.verificationSuccess);
export const selectForgotPasswordSuccess = createSelector(selectAuthState, (state) => state.forgotPasswordSuccess);
export const selectResetPasswordSuccess = createSelector(selectAuthState, (state) => state.resetPasswordSuccess);
export const selectUserRole = createSelector(selectCurrentUser, (user) => user?.role ?? null);
export const selectIsAdmin = createSelector(selectUserRole, (role) => role === 'ROLE_ADMIN');
