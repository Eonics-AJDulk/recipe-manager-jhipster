import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'recipe',
        data: { pageTitle: 'recipemanagerApp.recipe.home.title' },
        loadChildren: () => import('./recipe/recipe.module').then(m => m.RecipeModule),
      },
      {
        path: 'ingredient',
        data: { pageTitle: 'recipemanagerApp.ingredient.home.title' },
        loadChildren: () => import('./ingredient/ingredient.module').then(m => m.IngredientModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
