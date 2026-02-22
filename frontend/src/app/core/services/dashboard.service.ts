import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { DashboardSummary } from '../models/dashboard.model';
import { MonthlyTrend } from '../models/analytics.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/dashboard`;

  getSummary(year: number, month: number): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${this.apiUrl}/summary`, {
      params: new HttpParams().set('year', year).set('month', month),
    });
  }

  getMonthlyTrends(year: number): Observable<{ data: MonthlyTrend[] }> {
    return this.http.get<{ data: MonthlyTrend[] }>(`${this.apiUrl}/analytics`, {
      params: new HttpParams().set('year', year),
    });
  }
}
