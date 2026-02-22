import { createReducer, on } from '@ngrx/store';
import { initialCategoryState } from './category.state';
import * as CategoryActions from './category.actions';

export const categoryReducer = createReducer(
  initialCategoryState,

  on(CategoryActions.loadCategories, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(CategoryActions.loadCategoriesSuccess, (state, { categories }) => ({
    ...state,
    loading: false,
    categories,
  })),
  on(CategoryActions.loadCategoriesFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(CategoryActions.createCategory, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(CategoryActions.createCategorySuccess, (state, { category }) => ({
    ...state,
    loading: false,
    categories: [...state.categories, category],
  })),
  on(CategoryActions.createCategoryFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  on(CategoryActions.deleteCategory, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(CategoryActions.deleteCategorySuccess, (state, { id }) => ({
    ...state,
    loading: false,
    categories: state.categories.filter((c) => c.id !== id),
  })),
  on(CategoryActions.deleteCategoryFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),
);
