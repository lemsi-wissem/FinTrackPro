import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { BudgetService } from '../../core/services/budget.service';
import * as BudgetActions from './budget.actions';

@Injectable()
export class BudgetEffects {
  constructor(
    private actions$: Actions,
    private budgetService: BudgetService,
  ) {}

  loadBudgets$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BudgetActions.loadBudgets),
      switchMap(({ year, month }) =>
        this.budgetService.getForPeriod(year, month).pipe(
          map((budgets) => BudgetActions.loadBudgetsSuccess({ budgets })),
          catchError((err) =>
            of(
              BudgetActions.loadBudgetsFailure({
                error: err.error?.message || 'Failed to load budgets',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  upsertBudget$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BudgetActions.upsertBudget),
      switchMap(({ request }) =>
        this.budgetService.upsert(request).pipe(
          map((budget) => BudgetActions.upsertBudgetSuccess({ budget })),
          catchError((err) =>
            of(
              BudgetActions.upsertBudgetFailure({
                error: err.error?.message || 'Failed to save budget',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  upsertBudgetSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BudgetActions.upsertBudgetSuccess),
      map(({ budget }) =>
        BudgetActions.loadBudgets({
          year: budget.periodYear,
          month: budget.periodMonth ?? new Date().getMonth() + 1,
        }),
      ),
    ),
  );

  deleteBudget$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BudgetActions.deleteBudget),
      mergeMap(({ id }) =>
        this.budgetService.delete(id).pipe(
          map(() => BudgetActions.deleteBudgetSuccess({ id })),
          catchError((err) =>
            of(
              BudgetActions.deleteBudgetFailure({
                error: err.error?.message || 'Failed to delete budget',
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
