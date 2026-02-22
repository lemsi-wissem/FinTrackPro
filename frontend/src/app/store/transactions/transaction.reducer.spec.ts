import { transactionReducer } from './transaction.reducer';
import { initialTransactionState, TransactionState } from './transaction.state';
import * as TransactionActions from './transaction.actions';
import { Transaction, TransactionPageResponse } from '../../core/models/transaction.model';

describe('transactionReducer', () => {
  const makeTransaction = (id: string): Transaction => ({
    id,
    categoryId: null,
    categoryName: null,
    categoryColor: null,
    amount: 100,
    type: 'EXPENSE',
    description: 'Test',
    transactionDate: '2026-01-15',
    notes: null,
    attachmentUrl: null,
    createdAt: '2026-01-15T10:00:00Z',
  });

  const tx1 = makeTransaction('tx-1');
  const tx2 = makeTransaction('tx-2');
  const tx3 = makeTransaction('tx-3');

  it('should return initial state for an unknown action', () => {
    const action = { type: '@@UNKNOWN' } as any;
    const state = transactionReducer(undefined, action);
    expect(state).toEqual(initialTransactionState);
  });

  describe('loadTransactions', () => {
    it('should set loading=true and apply the filter', () => {
      const filter = { page: 1, size: 10, type: 'EXPENSE' as const };
      const action = TransactionActions.loadTransactions({ filter });
      const state = transactionReducer(initialTransactionState, action);

      expect(state.loading).toBeTrue();
      expect(state.error).toBeNull();
      expect(state.currentFilter).toEqual(filter);
    });
  });

  describe('loadTransactionsSuccess', () => {
    it('should populate transactions and pagination info', () => {
      const response: TransactionPageResponse = {
        content: [tx1, tx2],
        totalElements: 25,
        totalPages: 3,
        page: 0,
        size: 10,
      };
      const loadingState: TransactionState = { ...initialTransactionState, loading: true };
      const action = TransactionActions.loadTransactionsSuccess({ response });
      const state = transactionReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.transactions).toEqual([tx1, tx2]);
      expect(state.totalElements).toBe(25);
      expect(state.totalPages).toBe(3);
    });
  });

  describe('loadTransactionsFailure', () => {
    it('should set error and clear loading', () => {
      const loadingState: TransactionState = { ...initialTransactionState, loading: true };
      const action = TransactionActions.loadTransactionsFailure({ error: 'Server error' });
      const state = transactionReducer(loadingState, action);

      expect(state.loading).toBeFalse();
      expect(state.error).toBe('Server error');
    });
  });

  describe('createTransaction', () => {
    it('should set loading=true', () => {
      const action = TransactionActions.createTransaction({
        request: { amount: 100, type: 'EXPENSE', transactionDate: '2026-01-15' },
      });
      const state = transactionReducer(initialTransactionState, action);

      expect(state.loading).toBeTrue();
    });
  });

  describe('createTransactionSuccess', () => {
    it('should prepend transaction to list and increment totalElements', () => {
      const stateWithData: TransactionState = {
        ...initialTransactionState,
        transactions: [tx2, tx3],
        totalElements: 2,
        loading: true,
      };
      const action = TransactionActions.createTransactionSuccess({ transaction: tx1 });
      const state = transactionReducer(stateWithData, action);

      expect(state.loading).toBeFalse();
      expect(state.transactions[0]).toEqual(tx1);
      expect(state.transactions).toEqual([tx1, tx2, tx3]);
      expect(state.totalElements).toBe(3);
    });
  });

  describe('updateTransactionSuccess', () => {
    it('should replace the matching transaction in the list', () => {
      const updatedTx1: Transaction = { ...tx1, amount: 999, description: 'Updated' };
      const stateWithData: TransactionState = {
        ...initialTransactionState,
        transactions: [tx1, tx2, tx3],
        loading: true,
      };
      const action = TransactionActions.updateTransactionSuccess({ transaction: updatedTx1 });
      const state = transactionReducer(stateWithData, action);

      expect(state.loading).toBeFalse();
      expect(state.transactions[0]).toEqual(updatedTx1);
      expect(state.transactions[1]).toEqual(tx2);
      expect(state.transactions[2]).toEqual(tx3);
    });

    it('should not change list when id not found', () => {
      const unknownTx: Transaction = { ...tx1, id: 'unknown-id' };
      const stateWithData: TransactionState = {
        ...initialTransactionState,
        transactions: [tx1, tx2],
      };
      const action = TransactionActions.updateTransactionSuccess({ transaction: unknownTx });
      const state = transactionReducer(stateWithData, action);

      expect(state.transactions).toEqual([tx1, tx2]);
    });
  });

  describe('deleteTransaction', () => {
    it('should set loading=true', () => {
      const action = TransactionActions.deleteTransaction({ id: 'tx-1' });
      const state = transactionReducer(initialTransactionState, action);

      expect(state.loading).toBeTrue();
    });
  });

  describe('deleteTransactionSuccess', () => {
    it('should remove transaction from list and decrement totalElements', () => {
      const stateWithData: TransactionState = {
        ...initialTransactionState,
        transactions: [tx1, tx2, tx3],
        totalElements: 3,
        loading: true,
      };
      const action = TransactionActions.deleteTransactionSuccess({ id: 'tx-2' });
      const state = transactionReducer(stateWithData, action);

      expect(state.loading).toBeFalse();
      expect(state.transactions).toEqual([tx1, tx3]);
      expect(state.totalElements).toBe(2);
    });
  });

  describe('clearTransactionError', () => {
    it('should clear the error field', () => {
      const stateWithError: TransactionState = { ...initialTransactionState, error: 'some error' };
      const action = TransactionActions.clearTransactionError();
      const state = transactionReducer(stateWithError, action);

      expect(state.error).toBeNull();
    });
  });
});
