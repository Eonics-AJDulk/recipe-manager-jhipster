import { IIngredient } from 'app/entities/ingredient/ingredient.model';

export interface IRecipe {
  id: number;
  vegatarian?: boolean | null;
  nrOfServings?: number | null;
  instructions?: string | null;
  name?: string | null;
  ingredients?: Pick<IIngredient, 'id' | 'name'>[] | null;
}

export type NewRecipe = Omit<IRecipe, 'id'> & { id: null };
