import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="min-h-screen bg-neutral-50 flex">
      <!-- Left brand panel -->
      <div class="hidden lg:flex lg:w-1/2 bg-primary-600 text-white flex-col justify-center px-16">
        <div class="max-w-md">
          <h1 class="text-4xl font-bold mb-4">FinTrack Pro</h1>
          <p class="text-primary-100 text-lg leading-relaxed">
            Personal finance management, invoicing, and budget analytics — all in one place.
          </p>
          <div class="mt-10 space-y-4">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-primary-500 flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
                </svg>
              </div>
              <span class="text-primary-100">Track budgets & expenses</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-primary-500 flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
              </div>
              <span class="text-primary-100">Generate professional invoices</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-primary-500 flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
                </svg>
              </div>
              <span class="text-primary-100">Visualize spending trends</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right form panel -->
      <div class="w-full lg:w-1/2 flex items-center justify-center p-8">
        <div class="w-full max-w-md">
          <router-outlet />
        </div>
      </div>
    </div>
  `,
})
export class AuthLayoutComponent {}
