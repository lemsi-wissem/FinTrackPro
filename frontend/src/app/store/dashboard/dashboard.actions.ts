import { createAction, props } from '@ngrx/store';
import { DashboardSummary } from '../../core/models/dashboard.model';
import { MonthlyTrend } from '../../core/models/analytics.model';

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

export const loadMonthlyTrends = createAction(
  '[Dashboard] Load Monthly Trends',
  props<{ year: number }>(),
);
export const loadMonthlyTrendsSuccess = createAction(
  '[Dashboard] Load Monthly Trends Success',
  props<{ trends: MonthlyTrend[] }>(),
);
export const loadMonthlyTrendsFailure = createAction(
  '[Dashboard] Load Monthly Trends Failure',
  props<{ error: string }>(),
);
