import { User } from '../../core/models/user.model';

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  loading: boolean;
  error: string | null;
  registrationSuccess: boolean;
  verificationSuccess: boolean;
  forgotPasswordSuccess: boolean;
  resetPasswordSuccess: boolean;
}

export const initialAuthState: AuthState = {
  user: null,
  accessToken: null,
  loading: false,
  error: null,
  registrationSuccess: false,
  verificationSuccess: false,
  forgotPasswordSuccess: false,
  resetPasswordSuccess: false,
};
