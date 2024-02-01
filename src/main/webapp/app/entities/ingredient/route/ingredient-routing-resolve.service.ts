import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IIngredient } from '../ingredient.model';
import { IngredientService } from '../service/ingredient.service';

@Injectable({ providedIn: 'root' })
export class IngredientRoutingResolveService implements Resolve<IIngredient | null> {
  constructor(protected service: IngredientService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IIngredient | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ingredient: HttpResponse<IIngredient>) => {
          if (ingredient.body) {
            return of(ingredient.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
