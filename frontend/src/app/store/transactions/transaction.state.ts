import { Transaction, TransactionFilter } from '../../core/models/transaction.model';

export interface TransactionState {
  transactions: Transaction[];
  totalElements: number;
  totalPages: number;
  loading: boolean;
  error: string | null;
  currentFilter: TransactionFilter;
}

export const initialTransactionState: TransactionState = {
  transactions: [],
  totalElements: 0,
  totalPages: 0,
  loading: false,
  error: null,
  currentFilter: { page: 0, size: 20 },
};
