import { Component, Output, EventEmitter, inject, signal } from '@angular/core';
import { TransactionService } from '../../../core/services/transaction.service';
import { ImportResult } from '../../../core/models/transaction.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-csv-import',
  standalone: true,
  imports: [],
  template: `
    <div
      class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
      (click)="onBackdropClick($event)"
    >
      <div
        class="bg-white rounded-2xl shadow-xl w-full max-w-md"
        (click)="$event.stopPropagation()"
      >
        <!-- Header -->
        <div class="flex items-center justify-between px-6 py-4 border-b border-neutral-200">
          <h2 class="text-lg font-semibold text-neutral-900">Import Transactions</h2>
          <button (click)="cancelled.emit()" class="p-2 hover:bg-neutral-100 rounded-lg transition-colors">
            <svg class="w-5 h-5 text-neutral-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <div class="p-6 space-y-4">
          <!-- Description + template download -->
          <div class="bg-neutral-50 border border-neutral-200 rounded-xl p-4 text-sm text-neutral-600">
            <p class="mb-2 font-medium text-neutral-700">CSV Format</p>
            <p class="font-mono text-xs text-neutral-500 mb-3">date, type, amount, category, description, notes</p>
            <a
              [href]="templateUrl"
              download="import-template.csv"
              class="inline-flex items-center gap-1.5 text-primary-600 hover:text-primary-700 font-medium text-xs"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/>
              </svg>
              Download template
            </a>
          </div>

          <!-- File picker -->
          @if (!result()) {
            <div
              class="border-2 border-dashed border-neutral-300 rounded-xl p-6 text-center hover:border-primary-400 transition-colors cursor-pointer"
              (click)="fileInput.click()"
            >
              @if (selectedFile()) {
                <div class="flex items-center justify-center gap-2">
                  <svg class="w-5 h-5 text-success-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                  </svg>
                  <span class="text-sm font-medium text-neutral-700">{{ selectedFile()?.name }}</span>
                </div>
                <p class="text-xs text-neutral-400 mt-1">Click to change file</p>
              } @else {
                <svg class="w-8 h-8 text-neutral-300 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"/>
                </svg>
                <p class="text-sm text-neutral-500">Click to choose a CSV file</p>
              }
              <input
                #fileInput
                type="file"
                accept=".csv,text/csv"
                class="hidden"
                (change)="onFileSelected($event)"
              />
            </div>

            <!-- Actions -->
            <div class="flex gap-3">
              <button
                (click)="cancelled.emit()"
                class="flex-1 px-4 py-2.5 border border-neutral-200 rounded-lg text-sm font-medium hover:bg-neutral-50"
              >Cancel</button>
              <button
                (click)="doImport()"
                [disabled]="!selectedFile() || importing()"
                class="flex-1 px-4 py-2.5 bg-primary-600 text-white rounded-lg text-sm font-medium disabled:opacity-50 hover:bg-primary-700"
              >
                @if (importing()) { Importing... } @else { Import }
              </button>
            </div>
          }

          <!-- Result -->
          @if (result(); as r) {
            <div class="space-y-3">
              <div class="grid grid-cols-3 gap-2 text-center">
                <div class="bg-success-50 border border-success-200 rounded-lg p-3">
                  <p class="text-xl font-bold text-success-700">{{ r.imported }}</p>
                  <p class="text-xs text-success-600">Imported</p>
                </div>
                <div class="bg-warning-50 border border-warning-200 rounded-lg p-3">
                  <p class="text-xl font-bold text-warning-700">{{ r.skipped }}</p>
                  <p class="text-xs text-warning-600">Skipped</p>
                </div>
                <div class="bg-error-50 border border-error-200 rounded-lg p-3">
                  <p class="text-xl font-bold text-error-700">{{ r.errors.length }}</p>
                  <p class="text-xs text-error-600">Errors</p>
                </div>
              </div>

              @if (r.errors.length > 0) {
                <div class="bg-error-50 border border-error-200 rounded-lg p-3 max-h-32 overflow-y-auto">
                  @for (err of r.errors; track err.row) {
                    <p class="text-xs text-error-700">Row {{ err.row }}: {{ err.message }}</p>
                  }
                </div>
              }

              <button
                (click)="onDone(r)"
                class="w-full px-4 py-2.5 bg-primary-600 text-white rounded-lg text-sm font-medium hover:bg-primary-700"
              >Done</button>
            </div>
          }
        </div>
      </div>
    </div>
  `,
})
export class CsvImportComponent {
  @Output() imported = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  private readonly transactionService = inject(TransactionService);

  readonly selectedFile = signal<File | null>(null);
  readonly importing = signal(false);
  readonly result = signal<ImportResult | null>(null);

  readonly templateUrl = `${environment.apiUrl}/transactions/import/template`;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile.set(input.files[0]);
    }
  }

  doImport(): void {
    const file = this.selectedFile();
    if (!file) return;
    this.importing.set(true);
    this.transactionService.importCsv(file).subscribe({
      next: (res) => {
        this.importing.set(false);
        this.result.set(res);
      },
      error: () => {
        this.importing.set(false);
      },
    });
  }

  onDone(result: ImportResult): void {
    if (result.imported > 0) {
      this.imported.emit();
    } else {
      this.cancelled.emit();
    }
  }

  onBackdropClick(event: MouseEvent): void {
    this.cancelled.emit();
  }
}
