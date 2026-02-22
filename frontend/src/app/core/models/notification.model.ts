export interface Notification {
  id: string;
  type: string;
  message: string;
  referenceId: string | null;
  read: boolean;
  createdAt: string;
}
