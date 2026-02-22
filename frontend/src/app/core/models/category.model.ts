export type CategoryType = 'INCOME' | 'EXPENSE';

export interface Category {
  id: string;
  userId: string | null;
  name: string;
  type: CategoryType;
  color: string;
  icon: string;
  system: boolean;
}

export interface CreateCategoryRequest {
  name: string;
  type: CategoryType;
  color: string;
  icon: string;
}

export interface UpdateCategoryRequest {
  name: string;
  color: string;
  icon: string;
}
