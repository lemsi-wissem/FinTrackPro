import { createReducer, on } from '@ngrx/store';
import { initialDashboardState } from './dashboard.state';
import * as DashboardActions from './dashboard.actions';

export const dashboardReducer = createReducer(
  initialDashboardState,

  on(DashboardActions.loadDashboardSummary, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(DashboardActions.loadDashboardSummarySuccess, (state, { summary }) => ({
    ...state,
    loading: false,
    summary,
  })),
  on(DashboardActions.loadDashboardSummaryFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(DashboardActions.loadMonthlyTrends, (state) => ({
    ...state,
    trendsLoading: true,
  })),
  on(DashboardActions.loadMonthlyTrendsSuccess, (state, { trends }) => ({
    ...state,
    trendsLoading: false,
    monthlyTrends: trends,
  })),
  on(DashboardActions.loadMonthlyTrendsFailure, (state, { error }) => ({
    ...state,
    trendsLoading: false,
    error,
  })),
);
