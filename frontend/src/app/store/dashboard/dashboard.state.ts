import { DashboardSummary } from '../../core/models/dashboard.model';
import { MonthlyTrend } from '../../core/models/analytics.model';

export interface DashboardState {
  summary: DashboardSummary | null;
  monthlyTrends: MonthlyTrend[] | null;
  loading: boolean;
  trendsLoading: boolean;
  error: string | null;
}

export const initialDashboardState: DashboardState = {
  summary: null,
  monthlyTrends: null,
  loading: false,
  trendsLoading: false,
  error: null,
};
