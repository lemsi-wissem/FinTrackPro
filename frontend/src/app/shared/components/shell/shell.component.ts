import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { Store } from '@ngrx/store';
import { AsyncPipe, NgClass } from '@angular/common';
import { selectCurrentUser } from '../../../store/auth/auth.selectors';
import { selectNotifications, selectUnreadCount } from '../../../store/notifications/notification.selectors';
import * as AuthActions from '../../../store/auth/auth.actions';
import * as NotificationActions from '../../../store/notifications/notification.actions';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, AsyncPipe, NgClass],
  template: `
    <div class="flex h-screen bg-neutral-50">
      <!-- Sidebar -->
      <aside class="w-64 bg-white border-r border-neutral-200 flex flex-col shadow-sm">
        <!-- Logo -->
        <div class="h-16 flex items-center px-6 border-b border-neutral-200">
          <div class="flex items-center gap-2">
            <div class="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-sm">F</span>
            </div>
            <span class="text-lg font-bold text-neutral-900">FinTrack Pro</span>
          </div>
        </div>

        <!-- Navigation -->
        <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          <a routerLink="/dashboard" routerLinkActive="bg-primary-50 text-primary-700 font-medium"
             class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-neutral-600 hover:bg-neutral-50 hover:text-neutral-900 transition-colors text-sm">
            <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
            </svg>
            Dashboard
          </a>
          <a routerLink="/transactions" routerLinkActive="bg-primary-50 text-primary-700 font-medium"
             class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-neutral-600 hover:bg-neutral-50 hover:text-neutral-900 transition-colors text-sm">
            <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/>
            </svg>
            Transactions
          </a>
          <a routerLink="/budgets" routerLinkActive="bg-primary-50 text-primary-700 font-medium"
             class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-neutral-600 hover:bg-neutral-50 hover:text-neutral-900 transition-colors text-sm">
            <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
            </svg>
            Budgets
          </a>
          <a routerLink="/categories" routerLinkActive="bg-primary-50 text-primary-700 font-medium"
             class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-neutral-600 hover:bg-neutral-50 hover:text-neutral-900 transition-colors text-sm">
            <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A2 2 0 013 12V7a4 4 0 014-4z"/>
            </svg>
            Categories
          </a>
        </nav>

        <!-- Notification bell + User section -->
        <div class="p-4 border-t border-neutral-200">
          <!-- Notification bell -->
          <div class="relative mb-3">
            <button
              (click)="toggleNotifications()"
              class="w-full flex items-center gap-3 px-3 py-2 text-sm text-neutral-600 hover:text-neutral-900 hover:bg-neutral-50 rounded-lg transition-colors"
            >
              <div class="relative">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                </svg>
                @if ((unreadCount$ | async) ?? 0 > 0) {
                  <span class="absolute -top-1.5 -right-1.5 w-4 h-4 bg-error-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                    {{ (unreadCount$ | async) ?? 0 }}
                  </span>
                }
              </div>
              <span>Notifications</span>
            </button>

            <!-- Dropdown -->
            @if (showNotifications()) {
              <div class="absolute bottom-full left-0 right-0 mb-2 bg-white border border-neutral-200 rounded-xl shadow-lg z-50 max-h-80 overflow-y-auto">
                <div class="flex items-center justify-between px-4 py-3 border-b border-neutral-100">
                  <span class="text-sm font-semibold text-neutral-900">Notifications</span>
                  @if ((unreadCount$ | async) ?? 0 > 0) {
                    <button
                      (click)="markAllRead()"
                      class="text-xs text-primary-600 hover:text-primary-700 font-medium"
                    >Mark all read</button>
                  }
                </div>
                @if ((notifications$ | async)?.length === 0) {
                  <div class="py-6 text-center text-sm text-neutral-400">No notifications</div>
                } @else {
                  @for (n of notifications$ | async; track n.id) {
                    <div
                      class="flex items-start gap-3 px-4 py-3 hover:bg-neutral-50 cursor-pointer border-b border-neutral-50 last:border-0"
                      [ngClass]="n.read ? 'opacity-60' : ''"
                      (click)="onNotificationClick(n.id)"
                    >
                      <div class="w-2 h-2 rounded-full mt-1.5 flex-shrink-0"
                        [ngClass]="n.read ? 'bg-neutral-300' : 'bg-primary-500'"></div>
                      <p class="text-xs text-neutral-700 leading-relaxed">{{ n.message }}</p>
                    </div>
                  }
                }
              </div>
            }
          </div>

          @if (user$ | async; as user) {
            <div class="flex items-center gap-3 mb-3">
              <div class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center flex-shrink-0">
                <span class="text-primary-700 font-semibold text-sm">{{ user.firstName[0] }}{{ user.lastName[0] }}</span>
              </div>
              <div class="min-w-0">
                <p class="text-sm font-medium text-neutral-900 truncate">{{ user.firstName }} {{ user.lastName }}</p>
                <p class="text-xs text-neutral-500 truncate">{{ user.email }}</p>
              </div>
            </div>
          }
          <button (click)="logout()"
            class="w-full flex items-center gap-2 px-3 py-2 text-sm text-neutral-600 hover:text-neutral-900 hover:bg-neutral-50 rounded-lg transition-colors">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
            </svg>
            Sign out
          </button>
        </div>
      </aside>

      <!-- Main content -->
      <main class="flex-1 overflow-auto" (click)="closeNotifications()">
        <router-outlet />
      </main>
    </div>
  `,
})
export class ShellComponent implements OnInit {
  private readonly store = inject(Store);
  readonly user$ = this.store.select(selectCurrentUser);
  readonly notifications$ = this.store.select(selectNotifications);
  readonly unreadCount$ = this.store.select(selectUnreadCount);
  readonly showNotifications = signal(false);

  ngOnInit(): void {
    this.store.dispatch(NotificationActions.loadNotifications());
  }

  toggleNotifications(): void {
    this.showNotifications.update((v) => !v);
  }

  closeNotifications(): void {
    this.showNotifications.set(false);
  }

  onNotificationClick(id: string): void {
    this.store.dispatch(NotificationActions.markOneRead({ id }));
  }

  markAllRead(): void {
    this.store.dispatch(NotificationActions.markAllRead());
  }

  logout(): void {
    this.store.dispatch(AuthActions.logout());
  }
}
