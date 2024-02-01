import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IngredientFormService, IngredientFormGroup } from './ingredient-form.service';
import { IIngredient } from '../ingredient.model';
import { IngredientService } from '../service/ingredient.service';

@Component({
  selector: 'jhi-ingredient-update',
  templateUrl: './ingredient-update.component.html',
})
export class IngredientUpdateComponent implements OnInit {
  isSaving = false;
  ingredient: IIngredient | null = null;

  editForm: IngredientFormGroup = this.ingredientFormService.createIngredientFormGroup();

  constructor(
    protected ingredientService: IngredientService,
    protected ingredientFormService: IngredientFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ingredient }) => {
      this.ingredient = ingredient;
      if (ingredient) {
        this.updateForm(ingredient);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ingredient = this.ingredientFormService.getIngredient(this.editForm);
    if (ingredient.id !== null) {
      this.subscribeToSaveResponse(this.ingredientService.update(ingredient));
    } else {
      this.subscribeToSaveResponse(this.ingredientService.create(ingredient));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIngredient>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ingredient: IIngredient): void {
    this.ingredient = ingredient;
    this.ingredientFormService.resetForm(this.editForm, ingredient);
  }
}
