import { Component, OnInit, inject, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { AsyncPipe, CurrencyPipe, NgClass, DecimalPipe } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartConfiguration } from 'chart.js';
import {
  selectDashboardSummary,
  selectDashboardLoading,
  selectMonthlyTrends,
} from '../../store/dashboard/dashboard.selectors';
import {
  loadDashboardSummary,
  loadMonthlyTrends,
} from '../../store/dashboard/dashboard.actions';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, NgClass, DecimalPipe, RouterLink, BaseChartDirective],
  template: `
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-neutral-900">Dashboard</h1>
        <p class="text-neutral-500 text-sm mt-1">{{ monthName }} {{ currentYear }} overview</p>
      </div>

      @if (loading$ | async) {
        <!-- Loading skeleton -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          @for (i of [1, 2, 3]; track i) {
            <div class="bg-white rounded-xl p-6 border border-neutral-200 animate-pulse">
              <div class="h-4 bg-neutral-200 rounded w-24 mb-2"></div>
              <div class="h-8 bg-neutral-200 rounded w-32"></div>
            </div>
          }
        </div>
      }

      @if (summary$ | async; as summary) {
        <!-- Summary Cards -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div class="bg-white rounded-xl p-6 border border-neutral-200 shadow-sm">
            <p class="text-sm font-medium text-neutral-500 mb-1">Total Income</p>
            <p class="text-2xl font-bold text-success-600">
              {{ summary.totalIncome | currency: 'DZD' : 'symbol' : '1.2-2' }}
            </p>
            <div class="mt-2 flex items-center gap-1">
              <div class="w-2 h-2 rounded-full bg-success-400"></div>
              <span class="text-xs text-neutral-500">This month</span>
            </div>
          </div>
          <div class="bg-white rounded-xl p-6 border border-neutral-200 shadow-sm">
            <p class="text-sm font-medium text-neutral-500 mb-1">Total Expenses</p>
            <p class="text-2xl font-bold text-error-600">
              {{ summary.totalExpenses | currency: 'DZD' : 'symbol' : '1.2-2' }}
            </p>
            <div class="mt-2 flex items-center gap-1">
              <div class="w-2 h-2 rounded-full bg-error-400"></div>
              <span class="text-xs text-neutral-500">This month</span>
            </div>
          </div>
          <div
            class="bg-white rounded-xl p-6 border border-neutral-200 shadow-sm"
            [ngClass]="summary.netBalance >= 0 ? 'border-primary-200' : 'border-error-200'"
          >
            <p class="text-sm font-medium text-neutral-500 mb-1">Net Balance</p>
            <p
              class="text-2xl font-bold"
              [ngClass]="summary.netBalance >= 0 ? 'text-primary-600' : 'text-error-600'"
            >
              {{ summary.netBalance | currency: 'DZD' : 'symbol' : '1.2-2' }}
            </p>
            <div class="mt-2 flex items-center gap-1">
              <div class="w-2 h-2 rounded-full bg-primary-400"></div>
              <span class="text-xs text-neutral-500">Income - Expenses</span>
            </div>
          </div>
        </div>

        <!-- Charts Row -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <!-- Doughnut: spending by category -->
          <div class="bg-white rounded-xl border border-neutral-200 shadow-sm p-6">
            <h2 class="text-base font-semibold text-neutral-900 mb-4">Spending by Category</h2>
            @if (summary.topExpensesByCategory.length === 0) {
              <p class="text-sm text-neutral-400 text-center py-8">No expenses this month</p>
            } @else {
              <div class="h-56 flex items-center justify-center">
                <canvas baseChart
                  [data]="doughnutData()"
                  type="doughnut"
                  [options]="doughnutOptions">
                </canvas>
              </div>
            }
          </div>
          <!-- Bar: monthly income vs expenses -->
          <div class="bg-white rounded-xl border border-neutral-200 shadow-sm p-6">
            <h2 class="text-base font-semibold text-neutral-900 mb-4">
              Income vs Expenses — {{ currentYear }}
            </h2>
            <div class="h-56">
              <canvas baseChart
                [data]="barData()"
                type="bar"
                [options]="barOptions">
              </canvas>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- Top Expenses by Category -->
          <div class="bg-white rounded-xl border border-neutral-200 shadow-sm p-6">
            <h2 class="text-base font-semibold text-neutral-900 mb-4">Top Expense Categories</h2>
            @if (summary.topExpensesByCategory.length === 0) {
              <p class="text-sm text-neutral-400 text-center py-8">No expenses this month</p>
            } @else {
              <div class="space-y-4">
                @for (cat of summary.topExpensesByCategory; track cat.categoryId) {
                  <div>
                    <div class="flex items-center justify-between mb-1">
                      <div class="flex items-center gap-2">
                        <span
                          class="w-3 h-3 rounded-full flex-shrink-0"
                          [style.background-color]="cat.categoryColor"
                        ></span>
                        <span class="text-sm font-medium text-neutral-700">{{ cat.categoryName }}</span>
                      </div>
                      <div class="text-right">
                        <span class="text-sm font-semibold text-neutral-900">
                          {{ cat.amount | currency: 'DZD' : 'symbol' : '1.2-2' }}
                        </span>
                        <span class="text-xs text-neutral-400 ml-1">
                          {{ cat.percentage | number: '1.0-1' }}%
                        </span>
                      </div>
                    </div>
                    <div class="h-2 bg-neutral-100 rounded-full overflow-hidden">
                      <div
                        class="h-full rounded-full transition-all"
                        [style.width.%]="cat.percentage"
                        [style.background-color]="cat.categoryColor"
                      ></div>
                    </div>
                  </div>
                }
              </div>
            }
          </div>

          <!-- Recent Transactions -->
          <div class="bg-white rounded-xl border border-neutral-200 shadow-sm p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-base font-semibold text-neutral-900">Recent Transactions</h2>
              <a
                routerLink="/transactions"
                class="text-sm text-primary-600 hover:text-primary-700 font-medium"
              >
                View all &rarr;
              </a>
            </div>
            @if (summary.recentTransactions.length === 0) {
              <p class="text-sm text-neutral-400 text-center py-8">No transactions yet</p>
            } @else {
              <div class="space-y-3">
                @for (tx of summary.recentTransactions; track tx.id) {
                  <div class="flex items-center gap-3">
                    <div
                      class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0"
                      [style.background-color]="(tx.categoryColor || '#94A3B8') + '20'"
                    >
                      <span
                        class="w-3 h-3 rounded-full"
                        [style.background-color]="tx.categoryColor || '#94A3B8'"
                      ></span>
                    </div>
                    <div class="flex-1 min-w-0">
                      <p class="text-sm font-medium text-neutral-900 truncate">
                        {{ tx.description || tx.categoryName || 'Transaction' }}
                      </p>
                      <p class="text-xs text-neutral-400">{{ tx.transactionDate }}</p>
                    </div>
                    <span
                      class="text-sm font-semibold flex-shrink-0"
                      [ngClass]="tx.type === 'INCOME' ? 'text-success-600' : 'text-error-600'"
                    >
                      {{ tx.type === 'INCOME' ? '+' : '-'
                      }}{{ tx.amount | currency: 'DZD' : 'symbol' : '1.2-2' }}
                    </span>
                  </div>
                }
              </div>
            }
          </div>
        </div>
      }
    </div>
  `,
})
export class DashboardComponent implements OnInit {
  private readonly store = inject(Store);
  readonly summary$ = this.store.select(selectDashboardSummary);
  readonly loading$ = this.store.select(selectDashboardLoading);

  private readonly summary = toSignal(this.summary$);
  private readonly trends = toSignal(this.store.select(selectMonthlyTrends));

  readonly currentYear = new Date().getFullYear();
  readonly currentMonth = new Date().getMonth() + 1;
  readonly monthName = new Date().toLocaleString('default', { month: 'long' });

  private readonly MONTH_LABELS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                                    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  readonly doughnutData = computed<ChartConfiguration<'doughnut'>['data']>(() => {
    const cats = this.summary()?.topExpensesByCategory ?? [];
    return {
      labels: cats.map((c) => c.categoryName),
      datasets: [{
        data: cats.map((c) => c.amount),
        backgroundColor: cats.map((c) => c.categoryColor ?? '#94A3B8'),
        borderWidth: 2,
        borderColor: '#ffffff',
      }],
    };
  });

  readonly barData = computed<ChartConfiguration<'bar'>['data']>(() => {
    const t = this.trends() ?? [];
    return {
      labels: this.MONTH_LABELS,
      datasets: [
        {
          label: 'Income',
          data: t.map((m) => m.income),
          backgroundColor: '#10B981',
          borderRadius: 4,
        },
        {
          label: 'Expenses',
          data: t.map((m) => m.expenses),
          backgroundColor: '#EF4444',
          borderRadius: 4,
        },
      ],
    };
  });

  readonly doughnutOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'right', labels: { boxWidth: 12, font: { size: 12 } } } },
    cutout: '65%',
  };

  readonly barOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'top', labels: { boxWidth: 12, font: { size: 12 } } } },
    scales: { y: { beginAtZero: true, ticks: { font: { size: 11 } } } },
  };

  ngOnInit(): void {
    this.store.dispatch(loadDashboardSummary({ year: this.currentYear, month: this.currentMonth }));
    this.store.dispatch(loadMonthlyTrends({ year: this.currentYear }));
  }
}
