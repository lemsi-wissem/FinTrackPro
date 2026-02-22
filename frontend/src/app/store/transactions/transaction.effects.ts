import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { TransactionService } from '../../core/services/transaction.service';
import * as TransactionActions from './transaction.actions';

@Injectable()
export class TransactionEffects {
  constructor(
    private actions$: Actions,
    private transactionService: TransactionService,
  ) {}

  loadTransactions$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionActions.loadTransactions),
      switchMap(({ filter }) =>
        this.transactionService.getAll(filter).pipe(
          map((response) => TransactionActions.loadTransactionsSuccess({ response })),
          catchError((err) =>
            of(
              TransactionActions.loadTransactionsFailure({
                error: err.error?.message || 'Failed to load transactions',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  createTransaction$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionActions.createTransaction),
      switchMap(({ request }) =>
        this.transactionService.create(request).pipe(
          map((transaction) =>
            TransactionActions.createTransactionSuccess({ transaction }),
          ),
          catchError((err) =>
            of(
              TransactionActions.createTransactionFailure({
                error: err.error?.message || 'Failed to create transaction',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  updateTransaction$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionActions.updateTransaction),
      switchMap(({ id, request }) =>
        this.transactionService.update(id, request).pipe(
          map((transaction) =>
            TransactionActions.updateTransactionSuccess({ transaction }),
          ),
          catchError((err) =>
            of(
              TransactionActions.updateTransactionFailure({
                error: err.error?.message || 'Failed to update transaction',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  deleteTransaction$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionActions.deleteTransaction),
      mergeMap(({ id }) =>
        this.transactionService.delete(id).pipe(
          map(() => TransactionActions.deleteTransactionSuccess({ id })),
          catchError((err) =>
            of(
              TransactionActions.deleteTransactionFailure({
                error: err.error?.message || 'Failed to delete transaction',
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
