package nl.eonics.recipemanager.custom.service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import nl.eonics.recipemanager.custom.service.dto.RecipeWithIngredientsDTO;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * This class contains unit tests for the RecipeWithIngredientsDTO class.
 */
@Disabled
public class RecipeWithIngredientsDTOTest {

    /**
     * This test checks that the getRecipe method of the RecipeWithIngredientsDTO class
     * returns the correct RecipeDTO object.
     */
    @Test
    public void getRecipeReturnsCorrectRecipe() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        List<IngredientDTO> ingredients = List.of(new IngredientDTO());
        RecipeWithIngredientsDTO recipeWithIngredientsDTO = new RecipeWithIngredientsDTO(recipeDTO, ingredients);

        // When
        RecipeDTO actual = recipeWithIngredientsDTO.getRecipe();

        // Then
        assertEquals(recipeDTO, actual);
    }

    /**
     * This test checks that the getIngredients method of the RecipeWithIngredientsDTO class
     * returns the correct list of IngredientDTO objects.
     */
    @Test
    public void getIngredientsReturnsCorrectIngredients() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        List<IngredientDTO> ingredients = List.of(new IngredientDTO());
        RecipeWithIngredientsDTO recipeWithIngredientsDTO = new RecipeWithIngredientsDTO(recipeDTO, ingredients);

        // When
        List<IngredientDTO> actual = recipeWithIngredientsDTO.getIngredients();

        // Then
        assertEquals(ingredients, actual);
    }

    /**
     * This test checks that the getIngredients method of the RecipeWithIngredientsDTO class
     * returns an empty list when no ingredients are provided.
     */
    @Test
    public void getIngredientsReturnsEmptyListWhenNoIngredientsProvided() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        RecipeWithIngredientsDTO recipeWithIngredientsDTO = new RecipeWithIngredientsDTO(recipeDTO, Collections.emptyList());

        // When
        List<IngredientDTO> actual = recipeWithIngredientsDTO.getIngredients();

        // Then
        assertEquals(Collections.emptyList(), actual);
    }
}
