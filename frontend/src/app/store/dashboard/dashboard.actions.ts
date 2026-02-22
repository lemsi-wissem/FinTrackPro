import { createAction, props } from '@ngrx/store';
import { DashboardSummary } from '../../core/models/dashboard.model';

export const loadDashboardSummary = createAction(
  '[Dashboard] Load Summary',
  props<{ year: number; month: number }>(),
);
export const loadDashboardSummarySuccess = createAction(
  '[Dashboard] Load Summary Success',
  props<{ summary: DashboardSummary }>(),
);
export const loadDashboardSummaryFailure = createAction(
  '[Dashboard] Load Summary Failure',
  props<{ error: string }>(),
);
