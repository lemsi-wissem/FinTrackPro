import { Budget } from '../../core/models/budget.model';

export interface BudgetState {
  budgets: Budget[];
  loading: boolean;
  error: string | null;
}

export const initialBudgetState: BudgetState = {
  budgets: [],
  loading: false,
  error: null,
};
