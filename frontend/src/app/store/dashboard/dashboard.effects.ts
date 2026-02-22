import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { DashboardService } from '../../core/services/dashboard.service';
import * as DashboardActions from './dashboard.actions';

@Injectable()
export class DashboardEffects {
  constructor(
    private actions$: Actions,
    private dashboardService: DashboardService,
  ) {}

  loadDashboardSummary$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadDashboardSummary),
      switchMap(({ year, month }) =>
        this.dashboardService.getSummary(year, month).pipe(
          map((summary) =>
            DashboardActions.loadDashboardSummarySuccess({ summary }),
          ),
          catchError((err) =>
            of(
              DashboardActions.loadDashboardSummaryFailure({
                error: err.error?.message || 'Failed to load dashboard',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  loadMonthlyTrends$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadMonthlyTrends),
      switchMap(({ year }) =>
        this.dashboardService.getMonthlyTrends(year).pipe(
          map((res) => DashboardActions.loadMonthlyTrendsSuccess({ trends: res.data })),
          catchError((err) =>
            of(
              DashboardActions.loadMonthlyTrendsFailure({
                error: err.error?.message || 'Failed to load trends',
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
