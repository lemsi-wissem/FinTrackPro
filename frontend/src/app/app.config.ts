import { ApplicationConfig } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';

import { routes } from './app.routes';
import { reducers } from './store';
import { AuthEffects } from './store/auth/auth.effects';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';
import { environment } from './environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideAnimations(),
    provideStore(reducers),
    provideEffects([AuthEffects]),
    ...(!environment.production
      ? [provideStoreDevtools({ maxAge: 25, logOnly: false })]
      : []),
  ],
};
