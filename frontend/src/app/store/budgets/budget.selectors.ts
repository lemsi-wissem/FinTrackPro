import { createFeatureSelector, createSelector } from '@ngrx/store';
import { BudgetState } from './budget.state';

const selectBudgetState = createFeatureSelector<BudgetState>('budgets');

export const selectBudgets = createSelector(
  selectBudgetState,
  (s) => s.budgets,
);
export const selectBudgetsLoading = createSelector(
  selectBudgetState,
  (s) => s.loading,
);
export const selectOverBudgets = createSelector(selectBudgets, (budgets) =>
  budgets.filter((b) => b.percentageUsed >= 100),
);
