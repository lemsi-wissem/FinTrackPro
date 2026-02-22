import { createFeatureSelector, createSelector } from '@ngrx/store';
import { CategoryState } from './category.state';

const selectCategoryState =
  createFeatureSelector<CategoryState>('categories');

export const selectAllCategories = createSelector(
  selectCategoryState,
  (s) => s.categories,
);
export const selectIncomeCategories = createSelector(
  selectAllCategories,
  (cats) => cats.filter((c) => c.type === 'INCOME'),
);
export const selectExpenseCategories = createSelector(
  selectAllCategories,
  (cats) => cats.filter((c) => c.type === 'EXPENSE'),
);
export const selectCategoriesLoading = createSelector(
  selectCategoryState,
  (s) => s.loading,
);
