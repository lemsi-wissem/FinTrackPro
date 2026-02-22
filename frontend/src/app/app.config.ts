import { ApplicationConfig } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';

import { routes } from './app.routes';
import { reducers } from './store';
import { AuthEffects } from './store/auth/auth.effects';
import { TransactionEffects } from './store/transactions/transaction.effects';
import { CategoryEffects } from './store/categories/category.effects';
import { BudgetEffects } from './store/budgets/budget.effects';
import { DashboardEffects } from './store/dashboard/dashboard.effects';
import { NotificationEffects } from './store/notifications/notification.effects';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';
import { environment } from './environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideAnimations(),
    provideStore(reducers),
    provideEffects([
      AuthEffects,
      TransactionEffects,
      CategoryEffects,
      BudgetEffects,
      DashboardEffects,
      NotificationEffects,
    ]),
    provideCharts(withDefaultRegisterables()),
    ...(!environment.production
      ? [provideStoreDevtools({ maxAge: 25, logOnly: false })]
      : []),
  ],
};
