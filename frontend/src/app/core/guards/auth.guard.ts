import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { map, take } from 'rxjs/operators';
import { selectIsAuthenticated } from '../../store/auth/auth.selectors';
import { TokenService } from '../services/token.service';

export const authGuard: CanActivateFn = () => {
  const store = inject(Store);
  const router = inject(Router);
  const tokenService = inject(TokenService);

  if (tokenService.hasToken()) {
    return true;
  }

  return store.select(selectIsAuthenticated).pipe(
    take(1),
    map((isAuthenticated) => {
      if (isAuthenticated) return true;
      return router.createUrlTree(['/auth/login']);
    }),
  );
};
