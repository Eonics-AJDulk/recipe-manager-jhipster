package nl.eonics.recipemanager.custom.service.dto;

import java.util.List;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a Data Transfer Object (DTO) that represents a recipe with its ingredients.
 * It is used to transfer data between different parts of the application.
 */
public class RecipeWithIngredientsDTO {

    private final Logger log = LoggerFactory.getLogger(RecipeWithIngredientsDTO.class);

    // The recipe data
    private final RecipeDTO recipe;
    // The list of ingredients for the recipe
    private final List<IngredientDTO> ingredients;

    /**
     * Constructs a new RecipeWithIngredientsDTO with the given RecipeDTO and list of IngredientDTO.
     *
     * @param recipe      the recipe data
     * @param ingredients the list of ingredients for the recipe
     */
    public RecipeWithIngredientsDTO(RecipeDTO recipe, List<IngredientDTO> ingredients) {
        log.debug("Constructing RecipeWithIngredientsDTO with recipe: {} and ingredients: {}", recipe, ingredients);
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    /**
     * Gets the recipe data.
     *
     * @return the recipe data
     */
    public RecipeDTO getRecipe() {
        log.debug("Getting recipe: {}", recipe);
        return recipe;
    }

    /**
     * Gets the list of ingredients for the recipe.
     *
     * @return the list of ingredients for the recipe
     */
    public List<IngredientDTO> getIngredients() {
        log.debug("Getting ingredients: {}", ingredients);
        return ingredients;
    }
}
