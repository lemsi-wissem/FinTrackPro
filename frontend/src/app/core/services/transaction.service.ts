import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  Transaction,
  TransactionPageResponse,
  CreateTransactionRequest,
  UpdateTransactionRequest,
  TransactionFilter,
  ImportResult,
} from '../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/transactions`;

  getAll(filter: TransactionFilter): Observable<TransactionPageResponse> {
    let params = new HttpParams()
      .set('page', filter.page.toString())
      .set('size', filter.size.toString());
    if (filter.type) params = params.set('type', filter.type);
    if (filter.categoryId) params = params.set('categoryId', filter.categoryId);
    if (filter.from) params = params.set('from', filter.from);
    if (filter.to) params = params.set('to', filter.to);
    return this.http.get<TransactionPageResponse>(this.apiUrl, { params });
  }

  create(request: CreateTransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl, request);
  }

  update(id: string, request: UpdateTransactionRequest): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  importCsv(file: File): Observable<ImportResult> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ImportResult>(`${this.apiUrl}/import`, formData);
  }

  uploadAttachment(id: string, file: File): Observable<Transaction> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Transaction>(`${this.apiUrl}/${id}/attachment`, formData);
  }

  getImportTemplateUrl(): string {
    return `${this.apiUrl}/import/template`;
  }
}
