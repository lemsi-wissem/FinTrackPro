import { Component, OnInit, inject, signal } from '@angular/core';
import { Store } from '@ngrx/store';
import { AsyncPipe, NgClass } from '@angular/common';
import {
  ReactiveFormsModule,
  FormsModule,
  FormBuilder,
  Validators,
} from '@angular/forms';
import {
  selectIncomeCategories,
  selectExpenseCategories,
} from '../../../store/categories/category.selectors';
import {
  loadCategories,
  createCategory,
  deleteCategory,
} from '../../../store/categories/category.actions';
import { CreateCategoryRequest, CategoryType } from '../../../core/models/category.model';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [AsyncPipe, NgClass, ReactiveFormsModule, FormsModule],
  template: `
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-neutral-900">Categories</h1>
        <button
          (click)="showForm.set(true)"
          class="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 text-sm font-medium"
        >
          + Add Category
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Income Categories -->
        <div
          class="bg-white border border-neutral-200 rounded-xl overflow-hidden shadow-sm"
        >
          <div class="px-4 py-3 bg-success-50 border-b border-neutral-200">
            <h2 class="text-sm font-semibold text-success-700">
              &#8593; Income Categories
            </h2>
          </div>
          <div class="divide-y divide-neutral-50">
            @for (cat of incomeCategories$ | async; track cat.id) {
              <div class="flex items-center justify-between px-4 py-3">
                <div class="flex items-center gap-3">
                  <span
                    class="w-3 h-3 rounded-full flex-shrink-0"
                    [style.background-color]="cat.color"
                  ></span>
                  <span class="text-sm text-neutral-900">{{ cat.name }}</span>
                  @if (cat.system) {
                    <span
                      class="text-xs text-neutral-400 bg-neutral-100 px-2 py-0.5 rounded"
                    >System</span>
                  }
                </div>
                @if (!cat.system) {
                  <button
                    (click)="onDelete(cat.id)"
                    class="p-1 text-neutral-300 hover:text-error-500 rounded transition-colors"
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
                }
              </div>
            }
          </div>
        </div>

        <!-- Expense Categories -->
        <div
          class="bg-white border border-neutral-200 rounded-xl overflow-hidden shadow-sm"
        >
          <div class="px-4 py-3 bg-error-50 border-b border-neutral-200">
            <h2 class="text-sm font-semibold text-error-700">
              &#8595; Expense Categories
            </h2>
          </div>
          <div class="divide-y divide-neutral-50">
            @for (cat of expenseCategories$ | async; track cat.id) {
              <div class="flex items-center justify-between px-4 py-3">
                <div class="flex items-center gap-3">
                  <span
                    class="w-3 h-3 rounded-full flex-shrink-0"
                    [style.background-color]="cat.color"
                  ></span>
                  <span class="text-sm text-neutral-900">{{ cat.name }}</span>
                  @if (cat.system) {
                    <span
                      class="text-xs text-neutral-400 bg-neutral-100 px-2 py-0.5 rounded"
                    >System</span>
                  }
                </div>
                @if (!cat.system) {
                  <button
                    (click)="onDelete(cat.id)"
                    class="p-1 text-neutral-300 hover:text-error-500 rounded transition-colors"
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
                }
              </div>
            }
          </div>
        </div>
      </div>

      <!-- Add Category Modal -->
      @if (showForm()) {
        <div
          class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
        >
          <div class="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6">
            <h3 class="text-lg font-semibold text-neutral-900 mb-4">
              New Category
            </h3>
            <form
              [formGroup]="categoryForm"
              (ngSubmit)="onSubmit()"
              class="space-y-4"
            >
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 mb-1"
                >Name</label>
                <input
                  formControlName="name"
                  type="text"
                  placeholder="Category name"
                  class="w-full px-3 py-2.5 border border-neutral-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 mb-1"
                >Type</label>
                <div class="grid grid-cols-2 gap-2">
                  <button
                    type="button"
                    (click)="categoryForm.patchValue({ type: 'INCOME' })"
                    [ngClass]="
                      categoryForm.value.type === 'INCOME'
                        ? 'bg-success-50 border-success-400 text-success-700'
                        : 'border-neutral-200'
                    "
                    class="py-2 border-2 rounded-lg text-sm font-medium"
                  >
                    Income
                  </button>
                  <button
                    type="button"
                    (click)="categoryForm.patchValue({ type: 'EXPENSE' })"
                    [ngClass]="
                      categoryForm.value.type === 'EXPENSE'
                        ? 'bg-error-50 border-error-400 text-error-700'
                        : 'border-neutral-200'
                    "
                    class="py-2 border-2 rounded-lg text-sm font-medium"
                  >
                    Expense
                  </button>
                </div>
              </div>
              <div>
                <label
                  class="block text-sm font-medium text-neutral-700 mb-1"
                >Color</label>
                <div class="flex items-center gap-3">
                  <input
                    type="color"
                    formControlName="color"
                    class="w-10 h-10 rounded cursor-pointer border border-neutral-200"
                  />
                  <span class="text-sm text-neutral-600">{{
                    categoryForm.value.color
                  }}</span>
                </div>
              </div>
              <div class="flex gap-3">
                <button
                  type="button"
                  (click)="closeForm()"
                  class="flex-1 px-4 py-2.5 border border-neutral-200 rounded-lg text-sm hover:bg-neutral-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  [disabled]="categoryForm.invalid"
                  class="flex-1 px-4 py-2.5 bg-primary-600 text-white rounded-lg text-sm disabled:opacity-50"
                >
                  Create
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `,
})
export class CategoryListComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);

  readonly incomeCategories$ = this.store.select(selectIncomeCategories);
  readonly expenseCategories$ = this.store.select(selectExpenseCategories);
  readonly showForm = signal(false);

  categoryForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    type: ['EXPENSE' as CategoryType, Validators.required],
    color: ['#6366F1', Validators.required],
    icon: ['tag'],
  });

  ngOnInit(): void {
    this.store.dispatch(loadCategories());
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) return;
    const { name, type, color, icon } = this.categoryForm.value;
    const request: CreateCategoryRequest = {
      name: name!,
      type: type!,
      color: color!,
      icon: icon ?? 'tag',
    };
    this.store.dispatch(createCategory({ request }));
    this.closeForm();
  }

  closeForm(): void {
    this.showForm.set(false);
    this.categoryForm.reset({ type: 'EXPENSE', color: '#6366F1', icon: 'tag' });
  }

  onDelete(id: string): void {
    if (confirm('Delete this category?')) {
      this.store.dispatch(deleteCategory({ id }));
    }
  }
}
