import { dashboardReducer } from './dashboard.reducer';
import { initialDashboardState, DashboardState } from './dashboard.state';
import * as DashboardActions from './dashboard.actions';
import { DashboardSummary } from '../../core/models/dashboard.model';
import { MonthlyTrend } from '../../core/models/analytics.model';

describe('dashboardReducer', () => {
  const mockSummary: DashboardSummary = {
    totalIncome: 3000,
    totalExpenses: 1200,
    netBalance: 1800,
    topExpensesByCategory: [],
    recentTransactions: [],
  };

  const mockTrends: MonthlyTrend[] = Array.from({ length: 12 }, (_, i) => ({
    month: i + 1,
    income: 1000,
    expenses: 500,
  }));

  it('should return the initial state for an unknown action', () => {
    const action = { type: '@@UNKNOWN' } as any;
    const state = dashboardReducer(undefined, action);
    expect(state).toEqual(initialDashboardState);
  });

  describe('loadDashboardSummary', () => {
    it('should set loading to true and clear error', () => {
      const stateWithError: DashboardState = {
        ...initialDashboardState,
        error: 'Previous error',
        loading: false,
      };
      const action = DashboardActions.loadDashboardSummary({ year: 2026, month: 1 });
      const state = dashboardReducer(stateWithError, action);

      expect(state.loading).toBeTrue();
      expect(state.error).toBeNull();
    });

    it('should not change summary when loading begins', () => {
      const stateWithSummary: DashboardState = {
        ...initialDashboardState,
        summary: mockSummary,
      };
      const action = DashboardActions.loadDashboardSummary({ year: 2026, month: 1 });
      const state = dashboardReducer(stateWithSummary, action);

      expect(state.summary).toEqual(mockSummary);
    });
  });

  describe('loadDashboardSummarySuccess', () => {
    it('should set summary and clear loading', () => {
      const loadingState: DashboardState = { ...initialDashboardState, loading: true };
      const action = DashboardActions.loadDashboardSummarySuccess({ summary: mockSummary });
      const state = dashboardReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.summary).toEqual(mockSummary);
    });
  });

  describe('loadDashboardSummaryFailure', () => {
    it('should set error and clear loading', () => {
      const loadingState: DashboardState = { ...initialDashboardState, loading: true };
      const action = DashboardActions.loadDashboardSummaryFailure({ error: 'Network error' });
      const state = dashboardReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.error).toBe('Network error');
    });

    it('should not change summary on failure', () => {
      const stateWithSummary: DashboardState = {
        ...initialDashboardState,
        summary: mockSummary,
        loading: true,
      };
      const action = DashboardActions.loadDashboardSummaryFailure({ error: 'err' });
      const state = dashboardReducer(stateWithSummary, action);

      expect(state.summary).toEqual(mockSummary);
    });
  });

  describe('loadMonthlyTrends', () => {
    it('should set trendsLoading to true', () => {
      const action = DashboardActions.loadMonthlyTrends({ year: 2026 });
      const state = dashboardReducer(initialDashboardState, action);

      expect(state.trendsLoading).toBeTrue();
    });
  });

  describe('loadMonthlyTrendsSuccess', () => {
    it('should set monthlyTrends and clear trendsLoading', () => {
      const loadingState: DashboardState = { ...initialDashboardState, trendsLoading: true };
      const action = DashboardActions.loadMonthlyTrendsSuccess({ trends: mockTrends });
      const state = dashboardReducer(loadingState, action);

      expect(state.trendsLoading).toBeFalse();
      expect(state.monthlyTrends).toEqual(mockTrends);
      expect(state.monthlyTrends!.length).toBe(12);
    });
  });

  describe('loadMonthlyTrendsFailure', () => {
    it('should set error and clear trendsLoading', () => {
      const loadingState: DashboardState = { ...initialDashboardState, trendsLoading: true };
      const action = DashboardActions.loadMonthlyTrendsFailure({ error: 'Trends error' });
      const state = dashboardReducer(loadingState, action);

      expect(state.trendsLoading).toBeFalse();
      expect(state.error).toBe('Trends error');
    });
  });
});
