import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { TokenService } from '../services/token.service';
import { AuthService } from '../services/auth.service';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

const AUTH_URLS = ['/auth/login', '/auth/register', '/auth/refresh', '/auth/verify-email', '/auth/forgot-password', '/auth/reset-password'];

function isAuthUrl(url: string): boolean {
  return AUTH_URLS.some((authUrl) => url.includes(authUrl));
}

export const jwtInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const tokenService = inject(TokenService);
  const authService = inject(AuthService);
  const token = tokenService.getAccessToken();

  let authReq = req;
  if (token && !isAuthUrl(req.url)) {
    authReq = addTokenHeader(req, token);
  }

  return next(authReq).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && !isAuthUrl(req.url)) {
        return handle401Error(req, next, tokenService, authService);
      }
      return throwError(() => error);
    }),
  );
};

function addTokenHeader(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });
}

function handle401Error(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  tokenService: TokenService,
  authService: AuthService,
): Observable<any> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((response) => {
        isRefreshing = false;
        tokenService.setAccessToken(response.accessToken);
        refreshTokenSubject.next(response.accessToken);
        return next(addTokenHeader(req, response.accessToken));
      }),
      catchError((err) => {
        isRefreshing = false;
        tokenService.removeAccessToken();
        refreshTokenSubject.next(null);
        return throwError(() => err);
      }),
    );
  }

  return refreshTokenSubject.pipe(
    filter((token) => token !== null),
    take(1),
    switchMap((token) => next(addTokenHeader(req, token!))),
  );
}
