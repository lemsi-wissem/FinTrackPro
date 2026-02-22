import { createReducer, on } from '@ngrx/store';
import { initialNotificationState } from './notification.state';
import * as NotificationActions from './notification.actions';

export const notificationReducer = createReducer(
  initialNotificationState,

  on(NotificationActions.loadNotifications, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),

  on(NotificationActions.loadNotificationsSuccess, (state, { notifications }) => ({
    ...state,
    loading: false,
    notifications,
    unreadCount: notifications.filter((n) => !n.read).length,
  })),

  on(NotificationActions.loadNotificationsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(NotificationActions.markOneReadSuccess, (state, { id }) => ({
    ...state,
    notifications: state.notifications.map((n) =>
      n.id === id ? { ...n, read: true } : n,
    ),
    unreadCount: Math.max(0, state.unreadCount - 1),
  })),

  on(NotificationActions.markAllReadSuccess, (state) => ({
    ...state,
    notifications: state.notifications.map((n) => ({ ...n, read: true })),
    unreadCount: 0,
  })),
);
