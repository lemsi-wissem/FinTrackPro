import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { CategoryService } from '../../core/services/category.service';
import * as CategoryActions from './category.actions';

@Injectable()
export class CategoryEffects {
  constructor(
    private actions$: Actions,
    private categoryService: CategoryService,
  ) {}

  loadCategories$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CategoryActions.loadCategories),
      switchMap(() =>
        this.categoryService.getAll().pipe(
          map((categories) =>
            CategoryActions.loadCategoriesSuccess({ categories }),
          ),
          catchError((err) =>
            of(
              CategoryActions.loadCategoriesFailure({
                error: err.error?.message || 'Failed to load categories',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  createCategory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CategoryActions.createCategory),
      switchMap(({ request }) =>
        this.categoryService.create(request).pipe(
          map((category) =>
            CategoryActions.createCategorySuccess({ category }),
          ),
          catchError((err) =>
            of(
              CategoryActions.createCategoryFailure({
                error: err.error?.message || 'Failed to create category',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  deleteCategory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CategoryActions.deleteCategory),
      mergeMap(({ id }) =>
        this.categoryService.delete(id).pipe(
          map(() => CategoryActions.deleteCategorySuccess({ id })),
          catchError((err) =>
            of(
              CategoryActions.deleteCategoryFailure({
                error: err.error?.message || 'Failed to delete category',
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
