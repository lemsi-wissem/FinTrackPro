import { createFeatureSelector, createSelector } from '@ngrx/store';
import { TransactionState } from './transaction.state';

const selectTransactionState =
  createFeatureSelector<TransactionState>('transactions');

export const selectTransactions = createSelector(
  selectTransactionState,
  (s) => s.transactions,
);
export const selectTransactionTotalElements = createSelector(
  selectTransactionState,
  (s) => s.totalElements,
);
export const selectTransactionTotalPages = createSelector(
  selectTransactionState,
  (s) => s.totalPages,
);
export const selectTransactionsLoading = createSelector(
  selectTransactionState,
  (s) => s.loading,
);
export const selectTransactionError = createSelector(
  selectTransactionState,
  (s) => s.error,
);
export const selectTransactionFilter = createSelector(
  selectTransactionState,
  (s) => s.currentFilter,
);
