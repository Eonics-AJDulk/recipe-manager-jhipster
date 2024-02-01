import { IIngredient, NewIngredient } from './ingredient.model';

export const sampleWithRequiredData: IIngredient = {
  id: 37469,
};

export const sampleWithPartialData: IIngredient = {
  id: 41444,
};

export const sampleWithFullData: IIngredient = {
  id: 88937,
  name: 'global parsing',
};

export const sampleWithNewData: NewIngredient = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
