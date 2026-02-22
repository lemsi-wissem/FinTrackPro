import { createSelector, createFeatureSelector } from '@ngrx/store';
import { NotificationState } from './notification.state';

const selectNotificationState = createFeatureSelector<NotificationState>('notifications');

export const selectNotifications = createSelector(
  selectNotificationState,
  (state) => state.notifications,
);

export const selectUnreadCount = createSelector(
  selectNotificationState,
  (state) => state.unreadCount,
);

export const selectUnreadNotifications = createSelector(
  selectNotifications,
  (notifications) => notifications.filter((n) => !n.read),
);

export const selectNotificationsLoading = createSelector(
  selectNotificationState,
  (state) => state.loading,
);
