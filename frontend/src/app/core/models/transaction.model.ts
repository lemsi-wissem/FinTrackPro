export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Transaction {
  id: string;
  categoryId: string | null;
  categoryName: string | null;
  categoryColor: string | null;
  amount: number;
  type: TransactionType;
  description: string | null;
  transactionDate: string;
  notes: string | null;
  attachmentUrl: string | null;
  createdAt: string;
}

export interface CreateTransactionRequest {
  categoryId?: string;
  amount: number;
  type: TransactionType;
  description?: string;
  transactionDate: string;
  notes?: string;
}

export interface UpdateTransactionRequest extends CreateTransactionRequest {}

export interface TransactionPageResponse {
  content: Transaction[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface TransactionFilter {
  type?: TransactionType;
  categoryId?: string;
  from?: string;
  to?: string;
  page: number;
  size: number;
}

export interface ImportResult {
  imported: number;
  skipped: number;
  errors: Array<{ row: number; message: string }>;
}
