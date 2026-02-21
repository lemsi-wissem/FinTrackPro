import { Routes } from '@angular/router';
import { AuthLayoutComponent } from '../../shared/components/auth-layout/auth-layout.component';

export const authRoutes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      {
        path: 'login',
        loadComponent: () => import('./login/login.component').then((m) => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () => import('./register/register.component').then((m) => m.RegisterComponent),
      },
      {
        path: 'verify-email',
        loadComponent: () => import('./verify-email/verify-email.component').then((m) => m.VerifyEmailComponent),
      },
      {
        path: 'forgot-password',
        loadComponent: () => import('./forgot-password/forgot-password.component').then((m) => m.ForgotPasswordComponent),
      },
      {
        path: 'reset-password',
        loadComponent: () => import('./reset-password/reset-password.component').then((m) => m.ResetPasswordComponent),
      },
      { path: '', redirectTo: 'login', pathMatch: 'full' },
    ],
  },
];
