import { Component, OnInit, inject, signal } from '@angular/core';
import { Store } from '@ngrx/store';
import { AsyncPipe, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { selectCurrentUser, selectAuthLoading } from '../../store/auth/auth.selectors';
import { updateProfile } from '../../store/auth/auth.actions';
import { UserService } from '../../core/services/user.service';

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const pw = control.get('newPassword')?.value;
  const confirm = control.get('confirmPassword')?.value;
  return pw && confirm && pw !== confirm ? { passwordMismatch: true } : null;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [AsyncPipe, DatePipe, ReactiveFormsModule],
  template: `
    <div class="p-6 max-w-3xl mx-auto space-y-6">

      <!-- Header -->
      <div>
        <h1 class="text-2xl font-bold text-neutral-900">Profile & Settings</h1>
        <p class="text-neutral-500 text-sm mt-1">Manage your account details and password</p>
      </div>

      @if (user$ | async; as user) {

        <!-- Personal Info Card -->
        <div class="bg-white rounded-xl border border-neutral-200 shadow-sm p-6">
          <div class="flex items-center gap-4 mb-6">
            <!-- Avatar -->
            <div class="w-16 h-16 rounded-full bg-primary-100 flex items-center justify-center flex-shrink-0">
              <span class="text-xl font-bold text-primary-700">
                {{ (user.firstName[0] + user.lastName[0]).toUpperCase() }}
              </span>
            </div>
            <div>
              <p class="text-lg font-semibold text-neutral-900">{{ user.firstName }} {{ user.lastName }}</p>
              <p class="text-sm text-neutral-500">{{ user.email }}</p>
              <div class="flex items-center gap-2 mt-1">
                <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium
                  {{ user.role === 'ROLE_ADMIN' ? 'bg-error-100 text-error-700' :
                     user.role === 'ROLE_PREMIUM' ? 'bg-accent-100 text-accent-700' :
                     'bg-primary-100 text-primary-700' }}">
                  {{ user.role === 'ROLE_ADMIN' ? 'Admin' :
                     user.role === 'ROLE_PREMIUM' ? 'Premium' : 'User' }}
                </span>
                @if (user.verified) {
                  <span class="inline-flex items-center gap-1 text-xs text-success-600">
                    <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                    Verified
                  </span>
                }
                <span class="text-xs text-neutral-400">Member since {{ user.createdAt | date: 'MMM yyyy' }}</span>
              </div>
            </div>
          </div>

          <h2 class="text-sm font-semibold text-neutral-700 mb-4">Personal Information</h2>

          <form [formGroup]="profileForm" (ngSubmit)="onSaveProfile()" class="space-y-4">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-neutral-700 mb-1">First Name</label>
                <input
                  formControlName="firstName"
                  type="text"
                  class="w-full px-3 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="First name"
                />
                @if (profileForm.get('firstName')?.invalid && profileForm.get('firstName')?.touched) {
                  <p class="text-xs text-error-600 mt-1">First name is required</p>
                }
              </div>
              <div>
                <label class="block text-sm font-medium text-neutral-700 mb-1">Last Name</label>
                <input
                  formControlName="lastName"
                  type="text"
                  class="w-full px-3 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="Last name"
                />
                @if (profileForm.get('lastName')?.invalid && profileForm.get('lastName')?.touched) {
                  <p class="text-xs text-error-600 mt-1">Last name is required</p>
                }
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-neutral-700 mb-1">Email Address</label>
              <input
                type="text"
                [value]="user.email"
                disabled
                class="w-full px-3 py-2 border border-neutral-200 rounded-lg text-sm bg-neutral-50 text-neutral-400 cursor-not-allowed"
              />
              <p class="text-xs text-neutral-400 mt-1">Email cannot be changed</p>
            </div>

            @if (profileSuccess()) {
              <div class="flex items-center gap-2 p-3 bg-success-50 border border-success-200 rounded-lg">
                <svg class="w-4 h-4 text-success-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                </svg>
                <p class="text-sm text-success-700">Profile updated successfully!</p>
              </div>
            }

            <div class="flex justify-end pt-2">
              <button
                type="submit"
                [disabled]="profileForm.invalid || profileForm.pristine || (loading$ | async)"
                class="px-5 py-2 bg-primary-600 text-white text-sm font-medium rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                @if (loading$ | async) { Saving... } @else { Save Changes }
              </button>
            </div>
          </form>
        </div>

        <!-- Security Card -->
        <div class="bg-white rounded-xl border border-neutral-200 shadow-sm p-6">
          <h2 class="text-sm font-semibold text-neutral-700 mb-4">Change Password</h2>

          <form [formGroup]="passwordForm" (ngSubmit)="onChangePassword()" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-neutral-700 mb-1">Current Password</label>
              <input
                formControlName="currentPassword"
                type="password"
                class="w-full px-3 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Enter current password"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-neutral-700 mb-1">New Password</label>
              <input
                formControlName="newPassword"
                type="password"
                class="w-full px-3 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="At least 8 characters"
              />
              @if (passwordForm.get('newPassword')?.hasError('minlength') && passwordForm.get('newPassword')?.touched) {
                <p class="text-xs text-error-600 mt-1">Password must be at least 8 characters</p>
              }
            </div>
            <div>
              <label class="block text-sm font-medium text-neutral-700 mb-1">Confirm New Password</label>
              <input
                formControlName="confirmPassword"
                type="password"
                class="w-full px-3 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="Repeat new password"
              />
              @if (passwordForm.hasError('passwordMismatch') && passwordForm.get('confirmPassword')?.touched) {
                <p class="text-xs text-error-600 mt-1">Passwords do not match</p>
              }
            </div>

            @if (passwordSuccess()) {
              <div class="flex items-center gap-2 p-3 bg-success-50 border border-success-200 rounded-lg">
                <svg class="w-4 h-4 text-success-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                </svg>
                <p class="text-sm text-success-700">Password changed successfully!</p>
              </div>
            }

            @if (passwordError()) {
              <div class="flex items-center gap-2 p-3 bg-error-50 border border-error-200 rounded-lg">
                <svg class="w-4 h-4 text-error-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
                </svg>
                <p class="text-sm text-error-700">{{ passwordError() }}</p>
              </div>
            }

            <div class="flex justify-end pt-2">
              <button
                type="submit"
                [disabled]="passwordForm.invalid || passwordSaving()"
                class="px-5 py-2 bg-primary-600 text-white text-sm font-medium rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                @if (passwordSaving()) { Saving... } @else { Change Password }
              </button>
            </div>
          </form>
        </div>

      } @else {
        <div class="bg-white rounded-xl border border-neutral-200 shadow-sm p-6 text-center">
          <p class="text-neutral-400 text-sm">Loading profile...</p>
        </div>
      }

    </div>
  `,
})
export class ProfileComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);

  readonly user$ = this.store.select(selectCurrentUser);
  readonly loading$ = this.store.select(selectAuthLoading);

  readonly profileSuccess = signal(false);
  readonly passwordSuccess = signal(false);
  readonly passwordError = signal<string | null>(null);
  readonly passwordSaving = signal(false);

  readonly profileForm = this.fb.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
  });

  readonly passwordForm = this.fb.group(
    {
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
    },
    { validators: passwordMatchValidator },
  );

  ngOnInit(): void {
    this.user$.subscribe((user) => {
      if (user) {
        this.profileForm.patchValue({ firstName: user.firstName, lastName: user.lastName });
      }
    });
  }

  onSaveProfile(): void {
    if (this.profileForm.invalid) return;
    const { firstName, lastName } = this.profileForm.getRawValue();
    this.store.dispatch(updateProfile({ request: { firstName: firstName!, lastName: lastName! } }));
    this.profileSuccess.set(true);
    this.profileForm.markAsPristine();
    setTimeout(() => this.profileSuccess.set(false), 3000);
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid) return;
    const { currentPassword, newPassword } = this.passwordForm.getRawValue();
    this.passwordSaving.set(true);
    this.passwordError.set(null);

    this.userService
      .changePassword({ currentPassword: currentPassword!, newPassword: newPassword! })
      .subscribe({
        next: () => {
          this.passwordSaving.set(false);
          this.passwordSuccess.set(true);
          this.passwordForm.reset();
          setTimeout(() => this.passwordSuccess.set(false), 3000);
        },
        error: (err) => {
          this.passwordSaving.set(false);
          this.passwordError.set(err.error?.message ?? 'Failed to change password');
        },
      });
  }
}
