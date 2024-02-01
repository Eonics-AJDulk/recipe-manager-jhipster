package nl.eonics.recipemanager.custom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import nl.eonics.recipemanager.custom.service.CustomRecipeService;
import nl.eonics.recipemanager.custom.service.dto.RecipeWithIngredientsDTO;
import nl.eonics.recipemanager.service.IngredientQueryService;
import nl.eonics.recipemanager.service.RecipeQueryService;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * This class contains unit tests for the CustomRecipeService class.
 */
@Disabled
public class CustomRecipeServiceTest {

    /**
     * The service that is being tested.
     */
    @InjectMocks
    private CustomRecipeService customRecipeService;

    /**
     * The RecipeQueryService that the CustomRecipeService depends on.
     * It is mocked so that its behavior can be controlled in the tests.
     */
    @Mock
    private RecipeQueryService recipeQueryService;

    /**
     * The IngredientQueryService that the CustomRecipeService depends on.
     * It is mocked so that its behavior can be controlled in the tests.
     */
    @Mock
    private IngredientQueryService ingredientQueryService;

    /**
     * This test checks that the searchRecipeWithIngredients method of the service
     * returns the expected results when ingredients are provided.
     */
    @Test
    public void searchRecipeWithIngredientsReturnsExpectedResultsWhenIngredientsAreProvided() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO ingredientDTO = new IngredientDTO();
        List<RecipeWithIngredientsDTO> expected = List.of(new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)));

        // When
        when(recipeQueryService.findByCriteria(any())).thenReturn(List.of(recipeDTO));
        when(ingredientQueryService.findByCriteria(any())).thenReturn(List.of(ingredientDTO));

        // Then
        List<RecipeWithIngredientsDTO> actual = customRecipeService.searchRecipeWithIngredients(
            "name",
            true,
            2,
            List.of("ingredient"),
            "instructions"
        );
        assertEquals(expected, actual);
    }

    /**
     * This test checks that the searchRecipeWithIngredients method of the service
     * returns an empty list when no ingredients are provided.
     */
    @Test
    public void searchRecipeWithIngredientsReturnsEmptyListWhenNoIngredientsAreProvided() {
        // Given
        List<RecipeWithIngredientsDTO> expected = List.of();

        // Then
        List<RecipeWithIngredientsDTO> actual = customRecipeService.searchRecipeWithIngredients("name", true, 2, null, "instructions");
        assertEquals(expected, actual);
    }

    /**
     * This test checks that the searchRecipeWithIngredients method of the service
     * returns an empty list when empty ingredients are provided.
     */
    @Test
    public void searchRecipeWithIngredientsReturnsEmptyListWhenEmptyIngredientsAreProvided() {
        // Given
        List<RecipeWithIngredientsDTO> expected = List.of();

        // Then
        List<RecipeWithIngredientsDTO> actual = customRecipeService.searchRecipeWithIngredients("name", true, 2, List.of(), "instructions");
        assertEquals(expected, actual);
    }
}
