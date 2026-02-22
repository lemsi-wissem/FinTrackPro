import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of, throwError } from 'rxjs';
import { Action } from '@ngrx/store';
import { DashboardEffects } from './dashboard.effects';
import { DashboardService } from '../../core/services/dashboard.service';
import * as DashboardActions from './dashboard.actions';
import { DashboardSummary } from '../../core/models/dashboard.model';
import { MonthlyTrend } from '../../core/models/analytics.model';

describe('DashboardEffects', () => {
  let actions$: Observable<Action>;
  let effects: DashboardEffects;
  let dashboardService: jasmine.SpyObj<DashboardService>;

  const mockSummary: DashboardSummary = {
    totalIncome: 5000,
    totalExpenses: 2000,
    netBalance: 3000,
    topExpensesByCategory: [],
    recentTransactions: [],
  };

  const mockTrends: MonthlyTrend[] = Array.from({ length: 12 }, (_, i) => ({
    month: i + 1,
    income: 1000,
    expenses: 500,
  }));

  beforeEach(() => {
    const spy = jasmine.createSpyObj('DashboardService', [
      'getSummary',
      'getMonthlyTrends',
    ]);

    TestBed.configureTestingModule({
      providers: [
        DashboardEffects,
        provideMockActions(() => actions$),
        { provide: DashboardService, useValue: spy },
      ],
    });

    effects = TestBed.inject(DashboardEffects);
    dashboardService = TestBed.inject(DashboardService) as jasmine.SpyObj<DashboardService>;
  });

  describe('loadDashboardSummary$', () => {
    it('should dispatch loadDashboardSummarySuccess on success', (done) => {
      actions$ = of(DashboardActions.loadDashboardSummary({ year: 2026, month: 1 }));
      dashboardService.getSummary.and.returnValue(of(mockSummary));

      effects.loadDashboardSummary$.subscribe((action) => {
        expect(action).toEqual(
          DashboardActions.loadDashboardSummarySuccess({ summary: mockSummary }),
        );
        done();
      });
    });

    it('should dispatch loadDashboardSummaryFailure on error', (done) => {
      actions$ = of(DashboardActions.loadDashboardSummary({ year: 2026, month: 1 }));
      const error = { error: { message: 'Server error' } };
      dashboardService.getSummary.and.returnValue(throwError(() => error));

      effects.loadDashboardSummary$.subscribe((action) => {
        expect(action).toEqual(
          DashboardActions.loadDashboardSummaryFailure({ error: 'Server error' }),
        );
        done();
      });
    });

    it('should use fallback error message when error.message is absent', (done) => {
      actions$ = of(DashboardActions.loadDashboardSummary({ year: 2026, month: 1 }));
      dashboardService.getSummary.and.returnValue(throwError(() => ({})));

      effects.loadDashboardSummary$.subscribe((action: any) => {
        expect(action.error).toBe('Failed to load dashboard');
        done();
      });
    });
  });

  describe('loadMonthlyTrends$', () => {
    it('should dispatch loadMonthlyTrendsSuccess on success', (done) => {
      actions$ = of(DashboardActions.loadMonthlyTrends({ year: 2026 }));
      dashboardService.getMonthlyTrends.and.returnValue(of({ data: mockTrends }));

      effects.loadMonthlyTrends$.subscribe((action) => {
        expect(action).toEqual(
          DashboardActions.loadMonthlyTrendsSuccess({ trends: mockTrends }),
        );
        done();
      });
    });

    it('should dispatch loadMonthlyTrendsFailure on error', (done) => {
      actions$ = of(DashboardActions.loadMonthlyTrends({ year: 2026 }));
      const error = { error: { message: 'Trends failed' } };
      dashboardService.getMonthlyTrends.and.returnValue(throwError(() => error));

      effects.loadMonthlyTrends$.subscribe((action) => {
        expect(action).toEqual(
          DashboardActions.loadMonthlyTrendsFailure({ error: 'Trends failed' }),
        );
        done();
      });
    });

    it('should use fallback error message for trends', (done) => {
      actions$ = of(DashboardActions.loadMonthlyTrends({ year: 2026 }));
      dashboardService.getMonthlyTrends.and.returnValue(throwError(() => ({})));

      effects.loadMonthlyTrends$.subscribe((action: any) => {
        expect(action.error).toBe('Failed to load trends');
        done();
      });
    });
  });
});
