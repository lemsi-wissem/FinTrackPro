import { Component, OnInit, inject, signal } from '@angular/core';
import { Store } from '@ngrx/store';
import { AsyncPipe, CurrencyPipe, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  selectTransactions,
  selectTransactionsLoading,
  selectTransactionTotalElements,
  selectTransactionTotalPages,
} from '../../../store/transactions/transaction.selectors';
import {
  loadTransactions,
  deleteTransaction,
} from '../../../store/transactions/transaction.actions';
import { loadCategories } from '../../../store/categories/category.actions';
import { Transaction, TransactionFilter, TransactionType } from '../../../core/models/transaction.model';
import { TransactionFormComponent } from '../transaction-form/transaction-form.component';
import { CsvImportComponent } from '../csv-import/csv-import.component';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [
    AsyncPipe,
    CurrencyPipe,
    NgClass,
    FormsModule,
    TransactionFormComponent,
    CsvImportComponent,
  ],
  template: `
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-bold text-neutral-900">Transactions</h1>
          <p class="text-neutral-500 text-sm mt-1">
            {{ (totalElements$ | async) ?? 0 }} total transactions
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button
            (click)="showImport.set(true)"
            class="flex items-center gap-2 px-4 py-2 border border-neutral-200 text-neutral-700 rounded-lg hover:bg-neutral-50 transition-colors text-sm font-medium"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"/>
            </svg>
            Import CSV
          </button>
          <button
            (click)="openCreateForm()"
            class="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors text-sm font-medium"
          >
            <svg
              class="w-4 h-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 4v16m8-8H4"
            />
          </svg>
          Add Transaction
        </button>
        </div>
      </div>

      <!-- Filters -->
      <div
        class="bg-white border border-neutral-200 rounded-xl p-4 mb-4 flex flex-wrap gap-3"
      >
        <select
          [(ngModel)]="filterType"
          (change)="applyFilter()"
          class="px-3 py-2 border border-neutral-200 rounded-lg text-sm bg-white text-neutral-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
        >
          <option value="">All types</option>
          <option value="INCOME">Income</option>
          <option value="EXPENSE">Expense</option>
        </select>
        <input
          type="date"
          [(ngModel)]="filterFrom"
          (change)="applyFilter()"
          placeholder="From date"
          class="px-3 py-2 border border-neutral-200 rounded-lg text-sm text-neutral-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
        />
        <input
          type="date"
          [(ngModel)]="filterTo"
          (change)="applyFilter()"
          placeholder="To date"
          class="px-3 py-2 border border-neutral-200 rounded-lg text-sm text-neutral-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
        />
        @if (filterType || filterFrom || filterTo) {
          <button
            (click)="clearFilters()"
            class="px-3 py-2 text-sm text-neutral-500 hover:text-neutral-700 underline"
          >
            Clear filters
          </button>
        }
      </div>

      <!-- Loading -->
      @if (loading$ | async) {
        <div
          class="bg-white border border-neutral-200 rounded-xl overflow-hidden"
        >
          @for (i of [1, 2, 3, 4, 5]; track i) {
            <div
              class="p-4 border-b border-neutral-100 animate-pulse flex items-center gap-4"
            >
              <div class="w-8 h-8 rounded-full bg-neutral-200"></div>
              <div class="flex-1 space-y-2">
                <div class="h-3 bg-neutral-200 rounded w-48"></div>
                <div class="h-2 bg-neutral-100 rounded w-24"></div>
              </div>
              <div class="h-4 bg-neutral-200 rounded w-20"></div>
            </div>
          }
        </div>
      } @else {
        <!-- Transactions table -->
        <div
          class="bg-white border border-neutral-200 rounded-xl overflow-hidden shadow-sm"
        >
          @if ((transactions$ | async)?.length === 0) {
            <div class="py-16 text-center">
              <p class="text-neutral-400 text-sm">No transactions found.</p>
              <button
                (click)="openCreateForm()"
                class="mt-2 text-primary-600 text-sm hover:underline"
              >
                Add your first transaction
              </button>
            </div>
          } @else {
            <table class="w-full">
              <thead class="bg-neutral-50 border-b border-neutral-200">
                <tr>
                  <th
                    class="text-left px-4 py-3 text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Date
                  </th>
                  <th
                    class="text-left px-4 py-3 text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Description
                  </th>
                  <th
                    class="text-left px-4 py-3 text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Category
                  </th>
                  <th
                    class="text-right px-4 py-3 text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                  >
                    Amount
                  </th>
                  <th class="px-4 py-3"></th>
                </tr>
              </thead>
              <tbody class="divide-y divide-neutral-100">
                @for (tx of transactions$ | async; track tx.id) {
                  <tr class="hover:bg-neutral-50 transition-colors">
                    <td
                      class="px-4 py-3 text-sm text-neutral-600 whitespace-nowrap"
                    >
                      {{ tx.transactionDate }}
                    </td>
                    <td class="px-4 py-3">
                      <p class="text-sm font-medium text-neutral-900">
                        {{ tx.description || '&mdash;' }}
                      </p>
                    </td>
                    <td class="px-4 py-3">
                      @if (tx.categoryName) {
                        <div class="flex items-center gap-2">
                          <span
                            class="w-2.5 h-2.5 rounded-full flex-shrink-0"
                            [style.background-color]="tx.categoryColor"
                          ></span>
                          <span class="text-sm text-neutral-600">{{
                            tx.categoryName
                          }}</span>
                        </div>
                      } @else {
                        <span class="text-sm text-neutral-400">&mdash;</span>
                      }
                    </td>
                    <td class="px-4 py-3 text-right">
                      <span
                        class="text-sm font-semibold"
                        [ngClass]="
                          tx.type === 'INCOME'
                            ? 'text-success-600'
                            : 'text-error-600'
                        "
                      >
                        {{ tx.type === 'INCOME' ? '+' : '-'
                        }}{{ tx.amount | currency: 'DZD' : 'symbol' : '1.2-2' }}
                      </span>
                    </td>
                    <td class="px-4 py-3">
                      <div class="flex items-center gap-1 justify-end">
                        <button
                          (click)="openEditForm(tx)"
                          class="p-1.5 text-neutral-400 hover:text-primary-600 hover:bg-primary-50 rounded transition-colors"
                        >
                          <svg
                            class="w-4 h-4"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              stroke-linecap="round"
                              stroke-linejoin="round"
                              stroke-width="2"
                              d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                            />
                          </svg>
                        </button>
                        <button
                          (click)="onDelete(tx.id)"
                          class="p-1.5 text-neutral-400 hover:text-error-600 hover:bg-error-50 rounded transition-colors"
                        >
                          <svg
                            class="w-4 h-4"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              stroke-linecap="round"
                              stroke-linejoin="round"
                              stroke-width="2"
                              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                            />
                          </svg>
                        </button>
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          }
        </div>

        <!-- Pagination -->
        @if (((totalPages$ | async) ?? 0) > 1) {
          <div class="flex items-center justify-center gap-2 mt-4">
            <button
              (click)="changePage(currentPage - 1)"
              [disabled]="currentPage === 0"
              class="px-3 py-1.5 border border-neutral-200 rounded-lg text-sm disabled:opacity-40 hover:bg-neutral-50"
            >
              &larr; Prev
            </button>
            <span class="text-sm text-neutral-600">
              Page {{ currentPage + 1 }} of {{ totalPages$ | async }}
            </span>
            <button
              (click)="changePage(currentPage + 1)"
              [disabled]="currentPage + 1 >= ((totalPages$ | async) ?? 0)"
              class="px-3 py-1.5 border border-neutral-200 rounded-lg text-sm disabled:opacity-40 hover:bg-neutral-50"
            >
              Next &rarr;
            </button>
          </div>
        }
      }

      <!-- Transaction Form Modal -->
      @if (showForm()) {
        <app-transaction-form
          [transaction]="editingTransaction()"
          (saved)="onFormSaved()"
          (cancelled)="closeForm()"
        >
        </app-transaction-form>
      }

      <!-- CSV Import Modal -->
      @if (showImport()) {
        <app-csv-import
          (imported)="onImported()"
          (cancelled)="showImport.set(false)"
        ></app-csv-import>
      }
    </div>
  `,
})
export class TransactionListComponent implements OnInit {
  private readonly store = inject(Store);

  readonly transactions$ = this.store.select(selectTransactions);
  readonly loading$ = this.store.select(selectTransactionsLoading);
  readonly totalElements$ = this.store.select(selectTransactionTotalElements);
  readonly totalPages$ = this.store.select(selectTransactionTotalPages);

  readonly showForm = signal(false);
  readonly showImport = signal(false);
  readonly editingTransaction = signal<Transaction | null>(null);

  currentPage = 0;
  filterType = '';
  filterFrom = '';
  filterTo = '';

  ngOnInit(): void {
    this.store.dispatch(loadCategories());
    this.loadTransactions();
  }

  loadTransactions(): void {
    const filter: TransactionFilter = {
      page: this.currentPage,
      size: 20,
      ...(this.filterType ? { type: this.filterType as TransactionType } : {}),
      ...(this.filterFrom ? { from: this.filterFrom } : {}),
      ...(this.filterTo ? { to: this.filterTo } : {}),
    };
    this.store.dispatch(loadTransactions({ filter }));
  }

  applyFilter(): void {
    this.currentPage = 0;
    this.loadTransactions();
  }

  clearFilters(): void {
    this.filterType = '';
    this.filterFrom = '';
    this.filterTo = '';
    this.applyFilter();
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadTransactions();
  }

  openCreateForm(): void {
    this.editingTransaction.set(null);
    this.showForm.set(true);
  }

  openEditForm(tx: Transaction): void {
    this.editingTransaction.set(tx);
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.editingTransaction.set(null);
  }

  onFormSaved(): void {
    this.closeForm();
    this.loadTransactions();
  }

  onImported(): void {
    this.showImport.set(false);
    this.loadTransactions();
  }

  onDelete(id: string): void {
    if (confirm('Delete this transaction?')) {
      this.store.dispatch(deleteTransaction({ id }));
      setTimeout(() => this.loadTransactions(), 500);
    }
  }
}
