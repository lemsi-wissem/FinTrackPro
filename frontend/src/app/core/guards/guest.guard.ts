import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token.service';

export const guestGuard: CanActivateFn = () => {
  const router = inject(Router);
  const tokenService = inject(TokenService);

  if (tokenService.hasToken()) {
    return router.createUrlTree(['/dashboard']);
  }
  return true;
};
