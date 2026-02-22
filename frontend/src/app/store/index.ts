import { ActionReducerMap } from '@ngrx/store';
import { AuthState } from './auth/auth.state';
import { TransactionState } from './transactions/transaction.state';
import { CategoryState } from './categories/category.state';
import { BudgetState } from './budgets/budget.state';
import { DashboardState } from './dashboard/dashboard.state';
import { NotificationState } from './notifications/notification.state';
import { authReducer } from './auth/auth.reducer';
import { transactionReducer } from './transactions/transaction.reducer';
import { categoryReducer } from './categories/category.reducer';
import { budgetReducer } from './budgets/budget.reducer';
import { dashboardReducer } from './dashboard/dashboard.reducer';
import { notificationReducer } from './notifications/notification.reducer';

export interface AppState {
  auth: AuthState;
  transactions: TransactionState;
  categories: CategoryState;
  budgets: BudgetState;
  dashboard: DashboardState;
  notifications: NotificationState;
}

export const reducers: ActionReducerMap<AppState> = {
  auth: authReducer,
  transactions: transactionReducer,
  categories: categoryReducer,
  budgets: budgetReducer,
  dashboard: dashboardReducer,
  notifications: notificationReducer,
};
