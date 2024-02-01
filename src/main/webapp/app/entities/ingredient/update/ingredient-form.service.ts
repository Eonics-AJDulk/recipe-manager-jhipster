import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IIngredient, NewIngredient } from '../ingredient.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IIngredient for edit and NewIngredientFormGroupInput for create.
 */
type IngredientFormGroupInput = IIngredient | PartialWithRequiredKeyOf<NewIngredient>;

type IngredientFormDefaults = Pick<NewIngredient, 'id'>;

type IngredientFormGroupContent = {
  id: FormControl<IIngredient['id'] | NewIngredient['id']>;
  name: FormControl<IIngredient['name']>;
};

export type IngredientFormGroup = FormGroup<IngredientFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class IngredientFormService {
  createIngredientFormGroup(ingredient: IngredientFormGroupInput = { id: null }): IngredientFormGroup {
    const ingredientRawValue = {
      ...this.getFormDefaults(),
      ...ingredient,
    };
    return new FormGroup<IngredientFormGroupContent>({
      id: new FormControl(
        { value: ingredientRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(ingredientRawValue.name),
    });
  }

  getIngredient(form: IngredientFormGroup): IIngredient | NewIngredient {
    return form.getRawValue() as IIngredient | NewIngredient;
  }

  resetForm(form: IngredientFormGroup, ingredient: IngredientFormGroupInput): void {
    const ingredientRawValue = { ...this.getFormDefaults(), ...ingredient };
    form.reset(
      {
        ...ingredientRawValue,
        id: { value: ingredientRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): IngredientFormDefaults {
    return {
      id: null,
    };
  }
}
