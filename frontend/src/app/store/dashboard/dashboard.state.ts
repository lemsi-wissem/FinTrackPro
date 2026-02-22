import { DashboardSummary } from '../../core/models/dashboard.model';

export interface DashboardState {
  summary: DashboardSummary | null;
  loading: boolean;
  error: string | null;
}

export const initialDashboardState: DashboardState = {
  summary: null,
  loading: false,
  error: null,
};
