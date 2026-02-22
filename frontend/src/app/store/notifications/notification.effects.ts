import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { NotificationService } from '../../core/services/notification.service';
import * as NotificationActions from './notification.actions';

@Injectable()
export class NotificationEffects {
  constructor(
    private actions$: Actions,
    private notificationService: NotificationService,
  ) {}

  load$ = createEffect(() =>
    this.actions$.pipe(
      ofType(NotificationActions.loadNotifications),
      switchMap(() =>
        this.notificationService.getAll().pipe(
          map((notifications) =>
            NotificationActions.loadNotificationsSuccess({ notifications }),
          ),
          catchError((err) =>
            of(
              NotificationActions.loadNotificationsFailure({
                error: err.error?.message || 'Failed to load notifications',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  markOne$ = createEffect(() =>
    this.actions$.pipe(
      ofType(NotificationActions.markOneRead),
      mergeMap(({ id }) =>
        this.notificationService.markOneRead(id).pipe(
          map(() => NotificationActions.markOneReadSuccess({ id })),
          catchError(() => of(NotificationActions.markOneReadSuccess({ id }))),
        ),
      ),
    ),
  );

  markAll$ = createEffect(() =>
    this.actions$.pipe(
      ofType(NotificationActions.markAllRead),
      switchMap(() =>
        this.notificationService.markAllRead().pipe(
          map(() => NotificationActions.markAllReadSuccess()),
          catchError(() => of(NotificationActions.markAllReadSuccess())),
        ),
      ),
    ),
  );
}
