import { createAction, props } from '@ngrx/store';
import { Budget, UpsertBudgetRequest } from '../../core/models/budget.model';

export const loadBudgets = createAction(
  '[Budgets] Load',
  props<{ year: number; month: number }>(),
);
export const loadBudgetsSuccess = createAction(
  '[Budgets] Load Success',
  props<{ budgets: Budget[] }>(),
);
export const loadBudgetsFailure = createAction(
  '[Budgets] Load Failure',
  props<{ error: string }>(),
);

export const upsertBudget = createAction(
  '[Budgets] Upsert',
  props<{ request: UpsertBudgetRequest }>(),
);
export const upsertBudgetSuccess = createAction(
  '[Budgets] Upsert Success',
  props<{ budget: Budget }>(),
);
export const upsertBudgetFailure = createAction(
  '[Budgets] Upsert Failure',
  props<{ error: string }>(),
);

export const deleteBudget = createAction(
  '[Budgets] Delete',
  props<{ id: string }>(),
);
export const deleteBudgetSuccess = createAction(
  '[Budgets] Delete Success',
  props<{ id: string }>(),
);
export const deleteBudgetFailure = createAction(
  '[Budgets] Delete Failure',
  props<{ error: string }>(),
);
