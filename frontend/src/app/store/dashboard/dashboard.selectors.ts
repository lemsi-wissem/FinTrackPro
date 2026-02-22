import { createFeatureSelector, createSelector } from '@ngrx/store';
import { DashboardState } from './dashboard.state';

const selectDashboardState =
  createFeatureSelector<DashboardState>('dashboard');

export const selectDashboardSummary = createSelector(
  selectDashboardState,
  (s) => s.summary,
);
export const selectDashboardLoading = createSelector(
  selectDashboardState,
  (s) => s.loading,
);
export const selectMonthlyTrends = createSelector(
  selectDashboardState,
  (s) => s.monthlyTrends,
);
