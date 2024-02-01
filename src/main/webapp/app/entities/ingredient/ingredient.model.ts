export interface IIngredient {
  id: number;
  name?: string | null;
}

export type NewIngredient = Omit<IIngredient, 'id'> & { id: null };
