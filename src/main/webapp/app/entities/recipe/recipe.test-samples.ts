import { IRecipe, NewRecipe } from './recipe.model';

export const sampleWithRequiredData: IRecipe = {
  id: 37149,
};

export const sampleWithPartialData: IRecipe = {
  id: 11375,
  vegatarian: true,
  nrOfServings: 86032,
  instructions: '../fake-data/blob/hipster.txt',
  name: 'bypassing neural Streets',
};

export const sampleWithFullData: IRecipe = {
  id: 46848,
  vegatarian: true,
  nrOfServings: 52136,
  instructions: '../fake-data/blob/hipster.txt',
  name: 'facilitate Metal Algeria',
};

export const sampleWithNewData: NewRecipe = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
