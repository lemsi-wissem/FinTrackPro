import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import * as AuthActions from '../../../store/auth/auth.actions';
import { selectAuthError, selectAuthLoading, selectVerificationSuccess } from '../../../store/auth/auth.selectors';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './verify-email.component.html',
})
export class VerifyEmailComponent implements OnInit {
  loading$ = this.store.select(selectAuthLoading);
  error$ = this.store.select(selectAuthError);
  success$ = this.store.select(selectVerificationSuccess);

  constructor(
    private route: ActivatedRoute,
    private store: Store,
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (token) {
      this.store.dispatch(AuthActions.verifyEmail({ token }));
    }
  }
}
