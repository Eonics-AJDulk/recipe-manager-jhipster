import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IIngredient } from '../ingredient.model';
import { IngredientService } from '../service/ingredient.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './ingredient-delete-dialog.component.html',
})
export class IngredientDeleteDialogComponent {
  ingredient?: IIngredient;

  constructor(protected ingredientService: IngredientService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ingredientService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
