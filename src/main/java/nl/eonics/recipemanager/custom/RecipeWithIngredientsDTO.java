package nl.eonics.recipemanager.custom;

import java.util.List;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;

public class RecipeWithIngredientsDTO {

    private final RecipeDTO recipe;
    private final List<IngredientDTO> ingredients;

    public RecipeWithIngredientsDTO(RecipeDTO recipe, List<IngredientDTO> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    public RecipeDTO getRecipe() {
        return recipe;
    }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }
}
