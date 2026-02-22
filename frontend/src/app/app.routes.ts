import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  {
    path: 'auth',
    canActivate: [guestGuard],
    loadChildren: () =>
      import('./features/auth/auth.routes').then((m) => m.authRoutes),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shared/components/shell/shell.component').then(
        (m) => m.ShellComponent,
      ),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent,
          ),
      },
      {
        path: 'transactions',
        loadComponent: () =>
          import(
            './features/transactions/transaction-list/transaction-list.component'
          ).then((m) => m.TransactionListComponent),
      },
      {
        path: 'budgets',
        loadComponent: () =>
          import('./features/budgets/budget-list/budget-list.component').then(
            (m) => m.BudgetListComponent,
          ),
      },
      {
        path: 'categories',
        loadComponent: () =>
          import(
            './features/categories/category-list/category-list.component'
          ).then((m) => m.CategoryListComponent),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: '/dashboard' },
];
