import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Budget, UpsertBudgetRequest } from '../models/budget.model';

@Injectable({ providedIn: 'root' })
export class BudgetService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/budgets`;

  getForPeriod(year: number, month: number): Observable<Budget[]> {
    return this.http.get<Budget[]>(this.apiUrl, {
      params: new HttpParams().set('year', year).set('month', month),
    });
  }

  upsert(request: UpsertBudgetRequest): Observable<Budget> {
    return this.http.put<Budget>(this.apiUrl, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
