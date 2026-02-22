import { createAction, props } from '@ngrx/store';
import { Notification } from '../../core/models/notification.model';

export const loadNotifications = createAction('[Notifications] Load');
export const loadNotificationsSuccess = createAction(
  '[Notifications] Load Success',
  props<{ notifications: Notification[] }>(),
);
export const loadNotificationsFailure = createAction(
  '[Notifications] Load Failure',
  props<{ error: string }>(),
);

export const markOneRead = createAction(
  '[Notifications] Mark One Read',
  props<{ id: string }>(),
);
export const markOneReadSuccess = createAction(
  '[Notifications] Mark One Read Success',
  props<{ id: string }>(),
);

export const markAllRead = createAction('[Notifications] Mark All Read');
export const markAllReadSuccess = createAction('[Notifications] Mark All Read Success');
