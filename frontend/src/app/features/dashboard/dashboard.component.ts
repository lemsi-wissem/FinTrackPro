import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { selectCurrentUser } from '../../store/auth/auth.selectors';
import * as AuthActions from '../../store/auth/auth.actions';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-neutral-50">
      <!-- Navbar -->
      <nav class="bg-white border-b border-neutral-200">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex justify-between h-16 items-center">
            <div class="flex items-center gap-2">
              <span class="text-xl font-bold text-primary-600">FinTrack Pro</span>
            </div>
            <div class="flex items-center gap-4">
              @if (user$ | async; as user) {
                <span class="text-sm text-neutral-600">{{ user.firstName }} {{ user.lastName }}</span>
              }
              <button
                (click)="onLogout()"
                class="text-sm text-neutral-500 hover:text-neutral-700 font-medium"
              >
                Sign out
              </button>
            </div>
          </div>
        </div>
      </nav>

      <!-- Content -->
      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div class="bg-white rounded-xl shadow-sm border border-neutral-200 p-8 text-center">
          <h1 class="text-2xl font-bold text-neutral-900 mb-2">Welcome to FinTrack Pro</h1>
          <p class="text-neutral-500">
            Your dashboard is ready. Budget tracking, transactions, and invoicing features are coming in Phase 2.
          </p>
        </div>
      </main>
    </div>
  `,
})
export class DashboardComponent {
  user$ = this.store.select(selectCurrentUser);

  constructor(private store: Store) {}

  onLogout(): void {
    this.store.dispatch(AuthActions.logout());
  }
}
