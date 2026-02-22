import { createAction, props } from '@ngrx/store';
import {
  Transaction,
  CreateTransactionRequest,
  UpdateTransactionRequest,
  TransactionFilter,
  TransactionPageResponse,
} from '../../core/models/transaction.model';

export const loadTransactions = createAction(
  '[Transactions] Load',
  props<{ filter: TransactionFilter }>(),
);
export const loadTransactionsSuccess = createAction(
  '[Transactions] Load Success',
  props<{ response: TransactionPageResponse }>(),
);
export const loadTransactionsFailure = createAction(
  '[Transactions] Load Failure',
  props<{ error: string }>(),
);

export const createTransaction = createAction(
  '[Transactions] Create',
  props<{ request: CreateTransactionRequest }>(),
);
export const createTransactionSuccess = createAction(
  '[Transactions] Create Success',
  props<{ transaction: Transaction }>(),
);
export const createTransactionFailure = createAction(
  '[Transactions] Create Failure',
  props<{ error: string }>(),
);

export const updateTransaction = createAction(
  '[Transactions] Update',
  props<{ id: string; request: UpdateTransactionRequest }>(),
);
export const updateTransactionSuccess = createAction(
  '[Transactions] Update Success',
  props<{ transaction: Transaction }>(),
);
export const updateTransactionFailure = createAction(
  '[Transactions] Update Failure',
  props<{ error: string }>(),
);

export const deleteTransaction = createAction(
  '[Transactions] Delete',
  props<{ id: string }>(),
);
export const deleteTransactionSuccess = createAction(
  '[Transactions] Delete Success',
  props<{ id: string }>(),
);
export const deleteTransactionFailure = createAction(
  '[Transactions] Delete Failure',
  props<{ error: string }>(),
);

export const clearTransactionError = createAction('[Transactions] Clear Error');
