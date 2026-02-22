import { Component, Input, Output, EventEmitter, OnInit, inject, signal } from '@angular/core';
import { Store } from '@ngrx/store';
import { AsyncPipe, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { selectAllCategories } from '../../../store/categories/category.selectors';
import {
  createTransaction,
  updateTransaction,
} from '../../../store/transactions/transaction.actions';
import { Transaction } from '../../../core/models/transaction.model';
import { Category } from '../../../core/models/category.model';
import { TransactionService } from '../../../core/services/transaction.service';

@Component({
  selector: 'app-transaction-form',
  standalone: true,
  imports: [ReactiveFormsModule, AsyncPipe, NgClass],
  template: `
    <!-- Modal Backdrop -->
    <div
      class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
      (click)="onBackdropClick($event)"
    >
      <div
        class="bg-white rounded-2xl shadow-xl w-full max-w-md"
        (click)="$event.stopPropagation()"
      >
        <!-- Header -->
        <div
          class="flex items-center justify-between px-6 py-4 border-b border-neutral-200"
        >
          <h2 class="text-lg font-semibold text-neutral-900">
            {{ transaction ? 'Edit' : 'New' }} Transaction
          </h2>
          <button
            (click)="cancelled.emit()"
            class="p-2 hover:bg-neutral-100 rounded-lg transition-colors"
          >
            <svg
              class="w-5 h-5 text-neutral-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <!-- Form -->
        <form
          [formGroup]="form"
          (ngSubmit)="onSubmit()"
          class="p-6 space-y-4"
        >
          <!-- Type -->
          <div>
            <label class="block text-sm font-medium text-neutral-700 mb-1"
              >Type</label
            >
            <div class="grid grid-cols-2 gap-2">
              <button
                type="button"
                (click)="form.patchValue({ type: 'INCOME' })"
                [ngClass]="
                  form.value.type === 'INCOME'
                    ? 'bg-success-50 border-success-400 text-success-700'
                    : 'border-neutral-200 text-neutral-600'
                "
                class="py-2.5 border-2 rounded-lg text-sm font-medium transition-all"
              >
                &#8593; Income
              </button>
              <button
                type="button"
                (click)="form.patchValue({ type: 'EXPENSE' })"
                [ngClass]="
                  form.value.type === 'EXPENSE'
                    ? 'bg-error-50 border-error-400 text-error-700'
                    : 'border-neutral-200 text-neutral-600'
                "
                class="py-2.5 border-2 rounded-lg text-sm font-medium transition-all"
              >
                &#8595; Expense
              </button>
            </div>
          </div>

          <!-- Amount -->
          <div>
            <label class="block text-sm font-medium text-neutral-700 mb-1"
              >Amount</label
            >
            <input
              type="number"
              formControlName="amount"
              step="0.01"
              min="0.01"
              placeholder="0.00"
              class="w-full px-3 py-2.5 border border-neutral-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <!-- Category -->
          <div>
            <label class="block text-sm font-medium text-neutral-700 mb-1"
              >Category (optional)</label
            >
            <select
              formControlName="categoryId"
              class="w-full px-3 py-2.5 border border-neutral-200 rounded-lg text-sm bg-white focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <option value="">— No category —</option>
              @for (cat of filteredCategories$ | async; track cat.id) {
                <option [value]="cat.id">{{ cat.name }}</option>
              }
            </select>
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium text-neutral-700 mb-1"
              >Description</label
            >
            <input
              type="text"
              formControlName="description"
              placeholder="e.g. Monthly rent"
              class="w-full px-3 py-2.5 border border-neutral-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <!-- Date -->
          <div>
            <label class="block text-sm font-medium text-neutral-700 mb-1"
              >Date</label
            >
            <input
              type="date"
              formControlName="transactionDate"
              class="w-full px-3 py-2.5 border border-neutral-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <!-- Notes -->
          <div>
            <label class="block text-sm font-medium text-neutral-700 mb-1"
              >Notes (optional)</label
            >
            <textarea
              formControlName="notes"
              rows="2"
              placeholder="Additional notes..."
              class="w-full px-3 py-2.5 border border-neutral-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
            ></textarea>
          </div>

          <!-- Attachment (edit mode only) -->
          @if (transaction) {
            <div>
              <label class="block text-sm font-medium text-neutral-700 mb-1">
                Receipt / Attachment
              </label>
              @if (transaction.attachmentUrl) {
                <a
                  [href]="transaction.attachmentUrl"
                  target="_blank"
                  class="inline-flex items-center gap-1.5 text-primary-600 hover:text-primary-700 text-xs font-medium mb-2"
                >
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13"/>
                  </svg>
                  View current attachment
                </a>
              }
              <label
                class="flex items-center gap-2 px-3 py-2.5 border border-dashed border-neutral-300 rounded-lg cursor-pointer hover:border-primary-400 transition-colors"
              >
                <svg class="w-4 h-4 text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13"/>
                </svg>
                <span class="text-sm text-neutral-500">
                  @if (pendingFile()) {
                    {{ pendingFile()!.name }}
                  } @else {
                    Choose file (JPG, PNG, PDF)
                  }
                </span>
                <input
                  type="file"
                  accept=".jpg,.jpeg,.png,.pdf"
                  class="hidden"
                  (change)="onFileSelected($event)"
                />
              </label>
            </div>
          }

          <!-- Actions -->
          <div class="flex gap-3 pt-2">
            <button
              type="button"
              (click)="cancelled.emit()"
              class="flex-1 px-4 py-2.5 border border-neutral-200 rounded-lg text-sm font-medium text-neutral-700 hover:bg-neutral-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              [disabled]="form.invalid"
              class="flex-1 px-4 py-2.5 bg-primary-600 text-white rounded-lg text-sm font-medium hover:bg-primary-700 disabled:opacity-50 transition-colors"
            >
              {{ transaction ? 'Update' : 'Create' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
})
export class TransactionFormComponent implements OnInit {
  @Input() transaction: Transaction | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);
  private readonly transactionService = inject(TransactionService);

  readonly pendingFile = signal<File | null>(null);

  private readonly allCategories$ = this.store.select(selectAllCategories);
  filteredCategories$!: Observable<Category[]>;

  form = this.fb.group({
    type: ['EXPENSE' as 'INCOME' | 'EXPENSE', Validators.required],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    categoryId: [''],
    description: [''],
    transactionDate: [
      new Date().toISOString().split('T')[0],
      Validators.required,
    ],
    notes: [''],
  });

  ngOnInit(): void {
    if (this.transaction) {
      this.form.patchValue({
        type: this.transaction.type,
        amount: this.transaction.amount,
        categoryId: this.transaction.categoryId ?? '',
        description: this.transaction.description ?? '',
        transactionDate: this.transaction.transactionDate,
        notes: this.transaction.notes ?? '',
      });
    }

    // Filter categories by selected type, reset categoryId on type change
    const typeControl = this.form.get('type')!;
    this.filteredCategories$ = combineLatest([
      this.allCategories$,
      typeControl.valueChanges,
    ]).pipe(
      map(([cats, type]) => cats.filter((c) => c.type === type)),
    );

    // Trigger initial value emission for filteredCategories$
    typeControl.updateValueAndValidity({ emitEvent: true });

    // Reset category when type changes
    typeControl.valueChanges.subscribe(() => {
      this.form.patchValue({ categoryId: '' });
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const { type, amount, categoryId, description, transactionDate, notes } =
      this.form.value;
    const request = {
      type: type!,
      amount: amount!,
      transactionDate: transactionDate!,
      ...(categoryId ? { categoryId } : {}),
      ...(description ? { description } : {}),
      ...(notes ? { notes } : {}),
    };

    if (this.transaction) {
      this.store.dispatch(
        updateTransaction({ id: this.transaction.id, request }),
      );
      const file = this.pendingFile();
      if (file) {
        this.transactionService.uploadAttachment(this.transaction.id, file).subscribe();
      }
    } else {
      this.store.dispatch(createTransaction({ request }));
    }
    setTimeout(() => this.saved.emit(), 300);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.pendingFile.set(input.files[0]);
    }
  }

  onBackdropClick(_event: MouseEvent): void {
    this.cancelled.emit();
  }
}
