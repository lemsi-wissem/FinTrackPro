import { createAction, props } from '@ngrx/store';
import {
  Category,
  CreateCategoryRequest,
} from '../../core/models/category.model';

export const loadCategories = createAction('[Categories] Load');
export const loadCategoriesSuccess = createAction(
  '[Categories] Load Success',
  props<{ categories: Category[] }>(),
);
export const loadCategoriesFailure = createAction(
  '[Categories] Load Failure',
  props<{ error: string }>(),
);

export const createCategory = createAction(
  '[Categories] Create',
  props<{ request: CreateCategoryRequest }>(),
);
export const createCategorySuccess = createAction(
  '[Categories] Create Success',
  props<{ category: Category }>(),
);
export const createCategoryFailure = createAction(
  '[Categories] Create Failure',
  props<{ error: string }>(),
);

export const deleteCategory = createAction(
  '[Categories] Delete',
  props<{ id: string }>(),
);
export const deleteCategorySuccess = createAction(
  '[Categories] Delete Success',
  props<{ id: string }>(),
);
export const deleteCategoryFailure = createAction(
  '[Categories] Delete Failure',
  props<{ error: string }>(),
);
