import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Store } from '@ngrx/store';
import { TokenService } from './core/services/token.service';
import * as AuthActions from './store/auth/auth.actions';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet />`,
})
export class AppComponent implements OnInit {
  constructor(
    private store: Store,
    private tokenService: TokenService,
  ) {}

  ngOnInit(): void {
    if (this.tokenService.hasToken()) {
      this.store.dispatch(AuthActions.loadCurrentUser());
    }
  }
}
