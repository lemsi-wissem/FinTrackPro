import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import * as AuthActions from '../../../store/auth/auth.actions';
import { selectAuthError, selectAuthLoading, selectResetPasswordSuccess } from '../../../store/auth/auth.selectors';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
})
export class ResetPasswordComponent implements OnInit {
  form!: FormGroup;
  loading$ = this.store.select(selectAuthLoading);
  error$ = this.store.select(selectAuthError);
  success$ = this.store.select(selectResetPasswordSuccess);
  showPassword = false;
  private token = '';

  constructor(
    private fb: FormBuilder,
    private store: Store,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.store.dispatch(AuthActions.clearAuthError());
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    this.form = this.fb.group(
      {
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator },
    );
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('newPassword');
    const confirm = control.get('confirmPassword');
    if (password && confirm && password.value !== confirm.value) {
      confirm.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.store.dispatch(
      AuthActions.resetPassword({
        request: { token: this.token, newPassword: this.form.value.newPassword },
      }),
    );
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  get f() {
    return this.form.controls;
  }
}
