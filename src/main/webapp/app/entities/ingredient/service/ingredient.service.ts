import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IIngredient, NewIngredient } from '../ingredient.model';

export type PartialUpdateIngredient = Partial<IIngredient> & Pick<IIngredient, 'id'>;

export type EntityResponseType = HttpResponse<IIngredient>;
export type EntityArrayResponseType = HttpResponse<IIngredient[]>;

@Injectable({ providedIn: 'root' })
export class IngredientService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ingredients');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/ingredients');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ingredient: NewIngredient): Observable<EntityResponseType> {
    return this.http.post<IIngredient>(this.resourceUrl, ingredient, { observe: 'response' });
  }

  update(ingredient: IIngredient): Observable<EntityResponseType> {
    return this.http.put<IIngredient>(`${this.resourceUrl}/${this.getIngredientIdentifier(ingredient)}`, ingredient, {
      observe: 'response',
    });
  }

  partialUpdate(ingredient: PartialUpdateIngredient): Observable<EntityResponseType> {
    return this.http.patch<IIngredient>(`${this.resourceUrl}/${this.getIngredientIdentifier(ingredient)}`, ingredient, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IIngredient>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IIngredient[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IIngredient[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getIngredientIdentifier(ingredient: Pick<IIngredient, 'id'>): number {
    return ingredient.id;
  }

  compareIngredient(o1: Pick<IIngredient, 'id'> | null, o2: Pick<IIngredient, 'id'> | null): boolean {
    return o1 && o2 ? this.getIngredientIdentifier(o1) === this.getIngredientIdentifier(o2) : o1 === o2;
  }

  addIngredientToCollectionIfMissing<Type extends Pick<IIngredient, 'id'>>(
    ingredientCollection: Type[],
    ...ingredientsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ingredients: Type[] = ingredientsToCheck.filter(isPresent);
    if (ingredients.length > 0) {
      const ingredientCollectionIdentifiers = ingredientCollection.map(ingredientItem => this.getIngredientIdentifier(ingredientItem)!);
      const ingredientsToAdd = ingredients.filter(ingredientItem => {
        const ingredientIdentifier = this.getIngredientIdentifier(ingredientItem);
        if (ingredientCollectionIdentifiers.includes(ingredientIdentifier)) {
          return false;
        }
        ingredientCollectionIdentifiers.push(ingredientIdentifier);
        return true;
      });
      return [...ingredientsToAdd, ...ingredientCollection];
    }
    return ingredientCollection;
  }
}
