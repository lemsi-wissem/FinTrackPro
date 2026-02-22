import { Transaction } from './transaction.model';

export interface CategorySpending {
  categoryId: string;
  categoryName: string;
  categoryColor: string;
  amount: number;
  percentage: number;
}

export interface DashboardSummary {
  totalIncome: number;
  totalExpenses: number;
  netBalance: number;
  topExpensesByCategory: CategorySpending[];
  recentTransactions: Transaction[];
}
