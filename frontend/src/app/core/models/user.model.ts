export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ROLE_USER' | 'ROLE_PREMIUM' | 'ROLE_ADMIN';
  verified: boolean;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface MessageResponse {
  message: string;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}
