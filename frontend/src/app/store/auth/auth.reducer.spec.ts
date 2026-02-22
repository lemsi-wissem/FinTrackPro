import { authReducer } from './auth.reducer';
import { initialAuthState, AuthState } from './auth.state';
import * as AuthActions from './auth.actions';
import { User, AuthResponse } from '../../core/models/user.model';

describe('authReducer', () => {
  const mockUser: User = {
    id: 'user-123',
    email: 'alice@example.com',
    firstName: 'Alice',
    lastName: 'Smith',
    role: 'ROLE_USER',
    verified: true,
    createdAt: '2026-01-01T00:00:00Z',
  };

  const mockAuthResponse: AuthResponse = {
    accessToken: 'jwt-token-abc',
    tokenType: 'Bearer',
  };

  it('should return the initial state for an unknown action', () => {
    const action = { type: '@@UNKNOWN' } as any;
    const state = authReducer(undefined, action);
    expect(state).toEqual(initialAuthState);
  });

  describe('login', () => {
    it('should set loading=true and clear error', () => {
      const stateWithError: AuthState = { ...initialAuthState, error: 'old error' };
      const action = AuthActions.login({ request: { email: 'a@b.com', password: 'pass' } });
      const state = authReducer(stateWithError, action);

      expect(state.loading).toBeTrue();
      expect(state.error).toBeNull();
    });
  });

  describe('loginSuccess', () => {
    it('should store accessToken and clear loading', () => {
      const loadingState: AuthState = { ...initialAuthState, loading: true };
      const action = AuthActions.loginSuccess({ response: mockAuthResponse });
      const state = authReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.accessToken).toBe('jwt-token-abc');
      expect(state.error).toBeNull();
    });
  });

  describe('loginFailure', () => {
    it('should set error and clear loading', () => {
      const loadingState: AuthState = { ...initialAuthState, loading: true };
      const action = AuthActions.loginFailure({ error: 'Invalid credentials' });
      const state = authReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.error).toBe('Invalid credentials');
    });
  });

  describe('register', () => {
    it('should set loading=true and reset registrationSuccess', () => {
      const prevState: AuthState = { ...initialAuthState, registrationSuccess: true };
      const action = AuthActions.register({
        request: { email: 'a@b.com', password: 'pass', firstName: 'A', lastName: 'B' },
      });
      const state = authReducer(prevState, action);

      expect(state.loading).toBeTrue();
      expect(state.registrationSuccess).toBeFalse();
    });
  });

  describe('registerSuccess', () => {
    it('should set registrationSuccess=true', () => {
      const loadingState: AuthState = { ...initialAuthState, loading: true };
      const action = AuthActions.registerSuccess({ message: 'Check your email' });
      const state = authReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.registrationSuccess).toBeTrue();
    });
  });

  describe('loadCurrentUserSuccess', () => {
    it('should store user and clear loading', () => {
      const loadingState: AuthState = { ...initialAuthState, loading: true };
      const action = AuthActions.loadCurrentUserSuccess({ user: mockUser });
      const state = authReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.user).toEqual(mockUser);
    });
  });

  describe('loadCurrentUserFailure', () => {
    it('should clear user and set error', () => {
      const stateWithUser: AuthState = { ...initialAuthState, user: mockUser };
      const action = AuthActions.loadCurrentUserFailure({ error: 'Unauthorized' });
      const state = authReducer(stateWithUser, action);

      expect(state.user).toBeNull();
      expect(state.error).toBe('Unauthorized');
    });
  });

  describe('refreshTokenSuccess', () => {
    it('should update accessToken', () => {
      const stateWithOldToken: AuthState = { ...initialAuthState, accessToken: 'old-token' };
      const action = AuthActions.refreshTokenSuccess({
        response: { accessToken: 'new-token', tokenType: 'Bearer' },
      });
      const state = authReducer(stateWithOldToken, action);

      expect(state.accessToken).toBe('new-token');
    });
  });

  describe('refreshTokenFailure', () => {
    it('should clear accessToken and user', () => {
      const loggedInState: AuthState = {
        ...initialAuthState,
        accessToken: 'some-token',
        user: mockUser,
      };
      const action = AuthActions.refreshTokenFailure();
      const state = authReducer(loggedInState, action);

      expect(state.accessToken).toBeNull();
      expect(state.user).toBeNull();
    });
  });

  describe('logoutSuccess', () => {
    it('should reset to initial state', () => {
      const loggedInState: AuthState = {
        ...initialAuthState,
        accessToken: 'token',
        user: mockUser,
        registrationSuccess: true,
      };
      const action = AuthActions.logoutSuccess();
      const state = authReducer(loggedInState, action);

      expect(state).toEqual(initialAuthState);
    });
  });

  describe('updateProfile', () => {
    it('should set loading=true', () => {
      const action = AuthActions.updateProfile({ request: { firstName: 'Bob', lastName: 'Jones' } });
      const state = authReducer(initialAuthState, action);

      expect(state.loading).toBeTrue();
      expect(state.error).toBeNull();
    });
  });

  describe('updateProfileSuccess', () => {
    it('should update user in state and clear loading', () => {
      const stateWithUser: AuthState = { ...initialAuthState, user: mockUser, loading: true };
      const updatedUser: User = { ...mockUser, firstName: 'Bob', lastName: 'Jones' };
      const action = AuthActions.updateProfileSuccess({ user: updatedUser });
      const state = authReducer(stateWithUser, action);

      expect(state.loading).toBeFalse();
      expect(state.user!.firstName).toBe('Bob');
      expect(state.user!.lastName).toBe('Jones');
    });
  });

  describe('updateProfileFailure', () => {
    it('should set error and clear loading', () => {
      const loadingState: AuthState = { ...initialAuthState, loading: true };
      const action = AuthActions.updateProfileFailure({ error: 'Update failed' });
      const state = authReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.error).toBe('Update failed');
    });
  });

  describe('clearAuthError', () => {
    it('should clear the error field', () => {
      const stateWithError: AuthState = { ...initialAuthState, error: 'Some error' };
      const action = AuthActions.clearAuthError();
      const state = authReducer(stateWithError, action);

      expect(state.error).toBeNull();
    });
  });
});
