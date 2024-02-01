import { IRecipe } from 'app/entities/recipe/recipe.model';

export interface IIngredient {
  id: number;
  name?: string | null;
  recipe?: Pick<IRecipe, 'id' | 'name'> | null;
}

export type NewIngredient = Omit<IIngredient, 'id'> & { id: null };
