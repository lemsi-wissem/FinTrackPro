import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { DashboardService } from './dashboard.service';
import { DashboardSummary } from '../models/dashboard.model';
import { MonthlyTrend } from '../models/analytics.model';

describe('DashboardService', () => {
  let service: DashboardService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1/dashboard';

  const mockSummary: DashboardSummary = {
    totalIncome: 5000,
    totalExpenses: 2000,
    netBalance: 3000,
    topExpensesByCategory: [
      {
        categoryId: 'cat-1',
        categoryName: 'Food',
        categoryColor: '#FF0000',
        amount: 800,
        percentage: 40,
      },
    ],
    recentTransactions: [],
  };

  const mockTrends: MonthlyTrend[] = Array.from({ length: 12 }, (_, i) => ({
    month: i + 1,
    income: 1000,
    expenses: 500,
  }));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DashboardService],
    });

    service = TestBed.inject(DashboardService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getSummary', () => {
    it('should GET /dashboard/summary with year and month params', () => {
      service.getSummary(2026, 1).subscribe((result) => {
        expect(result).toEqual(mockSummary);
      });

      const req = httpMock.expectOne(
        (r) =>
          r.url === `${BASE_URL}/summary` &&
          r.params.get('year') === '2026' &&
          r.params.get('month') === '1',
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockSummary);
    });

    it('should pass different year and month values correctly', () => {
      service.getSummary(2025, 12).subscribe();

      const req = httpMock.expectOne(
        (r) =>
          r.url === `${BASE_URL}/summary` &&
          r.params.get('year') === '2025' &&
          r.params.get('month') === '12',
      );
      req.flush(mockSummary);
    });

    it('should return the mapped DashboardSummary object', () => {
      let result: DashboardSummary | undefined;

      service.getSummary(2026, 3).subscribe((data) => {
        result = data;
      });

      const req = httpMock.expectOne((r) => r.url === `${BASE_URL}/summary`);
      req.flush(mockSummary);

      expect(result).toBeDefined();
      expect(result!.netBalance).toBe(3000);
      expect(result!.topExpensesByCategory.length).toBe(1);
    });
  });

  describe('getMonthlyTrends', () => {
    it('should GET /dashboard/analytics with year param', () => {
      service.getMonthlyTrends(2026).subscribe((result) => {
        expect(result.data).toEqual(mockTrends);
      });

      const req = httpMock.expectOne(
        (r) =>
          r.url === `${BASE_URL}/analytics` && r.params.get('year') === '2026',
      );
      expect(req.request.method).toBe('GET');
      req.flush({ data: mockTrends });
    });

    it('should return 12 monthly trend records', () => {
      let result: { data: MonthlyTrend[] } | undefined;

      service.getMonthlyTrends(2026).subscribe((data) => {
        result = data;
      });

      const req = httpMock.expectOne((r) => r.url === `${BASE_URL}/analytics`);
      req.flush({ data: mockTrends });

      expect(result!.data.length).toBe(12);
      expect(result!.data[0].month).toBe(1);
      expect(result!.data[11].month).toBe(12);
    });
  });
});
