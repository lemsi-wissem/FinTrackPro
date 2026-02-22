import { Component, OnInit, inject, signal } from '@angular/core';
import { Store } from '@ngrx/store';
import { AsyncPipe, CurrencyPipe, NgClass, DecimalPipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { selectBudgets, selectBudgetsLoading } from '../../../store/budgets/budget.selectors';
import { loadBudgets, upsertBudget, deleteBudget } from '../../../store/budgets/budget.actions';
import { loadCategories } from '../../../store/categories/category.actions';
import { selectExpenseCategories } from '../../../store/categories/category.selectors';

@Component({
  selector: 'app-budget-list',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, NgClass, DecimalPipe, FormsModule, ReactiveFormsModule],
  template: `
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-bold text-neutral-900">Budgets</h1>
          <p class="text-neutral-500 text-sm">Track your monthly spending limits</p>
        </div>
        <button
          (click)="showForm.set(true)"
          class="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 text-sm font-medium"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          Set Budget
        </button>
      </div>

      <!-- Month Selector -->
      <div class="flex items-center gap-3 mb-6">
        <button
          (click)="prevMonth()"
          class="p-2 border border-neutral-200 rounded-lg hover:bg-neutral-50"
        >
          <svg class="w-4 h-4 text-neutral-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <span class="text-sm font-semibold text-neutral-900 min-w-[140px] text-center">
          {{ monthLabel }}
        </span>
        <button
          (click)="nextMonth()"
          class="p-2 border border-neutral-200 rounded-lg hover:bg-neutral-50"
        >
          <svg class="w-4 h-4 text-neutral-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>

      <!-- Budgets Grid -->
      @if (loading$ | async) {
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          @for (i of [1, 2, 3]; track i) {
            <div class="bg-white border border-neutral-200 rounded-xl p-6 animate-pulse">
              <div class="h-4 bg-neutral-200 rounded w-24 mb-3"></div>
              <div class="h-2 bg-neutral-100 rounded mb-2"></div>
              <div class="h-6 bg-neutral-200 rounded w-32"></div>
            </div>
          }
        </div>
      } @else if ((budgets$ | async)?.length === 0) {
        <div class="bg-white border border-neutral-200 rounded-xl py-16 text-center">
          <p class="text-neutral-400">No budgets set for this period.</p>
          <button
            (click)="showForm.set(true)"
            class="mt-2 text-primary-600 text-sm hover:underline"
          >
            Create your first budget
          </button>
        </div>
      } @else {
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          @for (budget of budgets$ | async; track budget.id) {
            <div
              class="bg-white border border-neutral-200 rounded-xl p-6 shadow-sm"
              [ngClass]="
                budget.percentageUsed >= 100
                  ? 'border-error-300'
                  : budget.percentageUsed >= 75
                    ? 'border-warning-300'
                    : ''
              "
            >
              <!-- Category info -->
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                  <span
                    class="w-3 h-3 rounded-full"
                    [style.background-color]="budget.categoryColor"
                  ></span>
                  <span class="font-semibold text-neutral-900 text-sm">{{
                    budget.categoryName
                  }}</span>
                </div>
                <button
                  (click)="onDeleteBudget(budget.id)"
                  class="p-1.5 text-neutral-300 hover:text-error-500 transition-colors rounded"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                </button>
              </div>

              <!-- Progress bar -->
              <div class="h-2.5 bg-neutral-100 rounded-full overflow-hidden mb-3">
                <div
                  class="h-full rounded-full transition-all"
                  [style.width.%]="mathMin(budget.percentageUsed, 100)"
                  [ngClass]="
                    budget.percentageUsed >= 100
                      ? 'bg-error-500'
                      : budget.percentageUsed >= 75
                        ? 'bg-warning-500'
                        : 'bg-success-500'
                  "
                ></div>
              </div>

              <!-- Amounts -->
              <div class="flex items-end justify-between">
                <div>
                  <p class="text-xs text-neutral-400 mb-0.5">Spent</p>
                  <p
                    class="text-sm font-semibold"
                    [ngClass]="
                      budget.percentageUsed >= 100 ? 'text-error-600' : 'text-neutral-900'
                    "
                  >
                    {{ budget.spent | currency: 'DZD' : 'symbol' : '1.2-2' }}
                  </p>
                </div>
                <div class="text-right">
                  <p class="text-xs text-neutral-400 mb-0.5">Budget</p>
                  <p class="text-sm font-semibold text-neutral-900">
                    {{ budget.budgetAmount | currency: 'DZD' : 'symbol' : '1.2-2' }}
                  </p>
                </div>
              </div>

              <!-- Status badge -->
              @if (budget.percentageUsed >= 100) {
                <div class="mt-3 px-2 py-1 bg-error-50 rounded text-center">
                  <span class="text-xs font-medium text-error-600">
                    Over budget by
                    {{
                      budget.spent - budget.budgetAmount
                        | currency: 'DZD' : 'symbol' : '1.2-2'
                    }}
                  </span>
                </div>
              } @else {
                <p class="mt-2 text-xs text-neutral-400 text-center">
                  {{ budget.remaining | currency: 'DZD' : 'symbol' : '1.2-2' }} remaining
                  &middot; {{ budget.percentageUsed | number: '1.0-1' }}% used
                </p>
              }
            </div>
          }
        </div>
      }

      <!-- Add Budget Modal -->
      @if (showForm()) {
        <div class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div class="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6">
            <h3 class="text-lg font-semibold text-neutral-900 mb-4">Set Budget</h3>
            <form [formGroup]="budgetForm" (ngSubmit)="onBudgetSubmit()" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-neutral-700 mb-1">Category</label>
                <select
                  formControlName="categoryId"
                  class="w-full px-3 py-2.5 border border-neutral-200 rounded-lg text-sm bg-white focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value="">Select category</option>
                  @for (cat of expenseCategories$ | async; track cat.id) {
                    <option [value]="cat.id">{{ cat.name }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-neutral-700 mb-1">
                  Budget Amount
                </label>
                <input
                  type="number"
                  formControlName="amount"
                  step="0.01"
                  min="0.01"
                  placeholder="0.00"
                  class="w-full px-3 py-2.5 border border-neutral-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>
              <div class="flex gap-3">
                <button
                  type="button"
                  (click)="showForm.set(false)"
                  class="flex-1 px-4 py-2.5 border border-neutral-200 rounded-lg text-sm font-medium hover:bg-neutral-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  [disabled]="budgetForm.invalid"
                  class="flex-1 px-4 py-2.5 bg-primary-600 text-white rounded-lg text-sm font-medium disabled:opacity-50"
                >
                  Save
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `,
})
export class BudgetListComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);

  readonly budgets$ = this.store.select(selectBudgets);
  readonly loading$ = this.store.select(selectBudgetsLoading);
  readonly expenseCategories$ = this.store.select(selectExpenseCategories);
  readonly showForm = signal(false);

  currentYear = new Date().getFullYear();
  currentMonth = new Date().getMonth() + 1;

  get monthLabel(): string {
    return new Date(this.currentYear, this.currentMonth - 1, 1).toLocaleString(
      'default',
      { month: 'long', year: 'numeric' },
    );
  }

  budgetForm = this.fb.group({
    categoryId: ['', Validators.required],
    amount: [
      null as number | null,
      [Validators.required, Validators.min(0.01)],
    ],
  });

  ngOnInit(): void {
    this.store.dispatch(loadCategories());
    this.loadBudgets();
  }

  loadBudgets(): void {
    this.store.dispatch(
      loadBudgets({ year: this.currentYear, month: this.currentMonth }),
    );
  }

  prevMonth(): void {
    if (this.currentMonth === 1) {
      this.currentMonth = 12;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }
    this.loadBudgets();
  }

  nextMonth(): void {
    if (this.currentMonth === 12) {
      this.currentMonth = 1;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }
    this.loadBudgets();
  }

  onBudgetSubmit(): void {
    if (this.budgetForm.invalid) return;
    const { categoryId, amount } = this.budgetForm.value;
    this.store.dispatch(
      upsertBudget({
        request: {
          categoryId: categoryId!,
          amount: amount!,
          period: 'MONTHLY',
          periodYear: this.currentYear,
          periodMonth: this.currentMonth,
        },
      }),
    );
    this.showForm.set(false);
    this.budgetForm.reset();
  }

  onDeleteBudget(id: string): void {
    if (confirm('Delete this budget?')) {
      this.store.dispatch(deleteBudget({ id }));
    }
  }

  mathMin(a: number, b: number): number {
    return Math.min(a, b);
  }
}
