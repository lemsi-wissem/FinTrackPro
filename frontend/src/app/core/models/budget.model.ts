export type BudgetPeriod = 'WEEKLY' | 'MONTHLY' | 'YEARLY';

export interface Budget {
  id: string;
  categoryId: string;
  categoryName: string;
  categoryColor: string;
  budgetAmount: number;
  spent: number;
  remaining: number;
  percentageUsed: number;
  period: BudgetPeriod;
  periodYear: number;
  periodMonth: number | null;
}

export interface UpsertBudgetRequest {
  categoryId: string;
  amount: number;
  period: BudgetPeriod;
  periodYear: number;
  periodMonth?: number;
}
