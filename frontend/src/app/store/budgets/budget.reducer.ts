import { createReducer, on } from '@ngrx/store';
import { initialBudgetState } from './budget.state';
import * as BudgetActions from './budget.actions';

export const budgetReducer = createReducer(
  initialBudgetState,

  on(BudgetActions.loadBudgets, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(BudgetActions.loadBudgetsSuccess, (state, { budgets }) => ({
    ...state,
    loading: false,
    budgets,
  })),
  on(BudgetActions.loadBudgetsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(BudgetActions.upsertBudget, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(BudgetActions.upsertBudgetSuccess, (state, { budget }) => ({
    ...state,
    loading: false,
    budgets: state.budgets.some((b) => b.id === budget.id)
      ? state.budgets.map((b) => (b.id === budget.id ? budget : b))
      : [...state.budgets, budget],
  })),
  on(BudgetActions.upsertBudgetFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(BudgetActions.deleteBudget, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(BudgetActions.deleteBudgetSuccess, (state, { id }) => ({
    ...state,
    loading: false,
    budgets: state.budgets.filter((b) => b.id !== id),
  })),
  on(BudgetActions.deleteBudgetFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),
);
