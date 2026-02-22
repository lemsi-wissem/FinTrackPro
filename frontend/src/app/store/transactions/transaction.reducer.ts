import { createReducer, on } from '@ngrx/store';
import { initialTransactionState } from './transaction.state';
import * as TransactionActions from './transaction.actions';

export const transactionReducer = createReducer(
  initialTransactionState,

  on(TransactionActions.loadTransactions, (state, { filter }) => ({
    ...state,
    loading: true,
    error: null,
    currentFilter: filter,
  })),
  on(TransactionActions.loadTransactionsSuccess, (state, { response }) => ({
    ...state,
    loading: false,
    transactions: response.content,
    totalElements: response.totalElements,
    totalPages: response.totalPages,
  })),
  on(TransactionActions.loadTransactionsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(TransactionActions.createTransaction, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(TransactionActions.createTransactionSuccess, (state, { transaction }) => ({
    ...state,
    loading: false,
    transactions: [transaction, ...state.transactions],
    totalElements: state.totalElements + 1,
  })),
  on(TransactionActions.createTransactionFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(TransactionActions.updateTransaction, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(TransactionActions.updateTransactionSuccess, (state, { transaction }) => ({
    ...state,
    loading: false,
    transactions: state.transactions.map((t) =>
      t.id === transaction.id ? transaction : t,
    ),
  })),
  on(TransactionActions.updateTransactionFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(TransactionActions.deleteTransaction, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(TransactionActions.deleteTransactionSuccess, (state, { id }) => ({
    ...state,
    loading: false,
    transactions: state.transactions.filter((t) => t.id !== id),
    totalElements: state.totalElements - 1,
  })),
  on(TransactionActions.deleteTransactionFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(TransactionActions.clearTransactionError, (state) => ({
    ...state,
    error: null,
  })),
);
