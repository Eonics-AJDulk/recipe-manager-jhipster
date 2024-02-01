export interface IRecipe {
  id: number;
  vegatarian?: boolean | null;
  nrOfServings?: number | null;
  instructions?: string | null;
  name?: string | null;
}

export type NewRecipe = Omit<IRecipe, 'id'> & { id: null };
