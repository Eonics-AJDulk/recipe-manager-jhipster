package nl.eonics.recipemanager.custom.web.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import nl.eonics.recipemanager.custom.service.CustomRecipeService;
import nl.eonics.recipemanager.custom.service.dto.RecipeWithIngredientsDTO;
import nl.eonics.recipemanager.custom.web.rest.CustomController;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This class contains unit tests for the CustomController class.
 */
@Disabled
@ExtendWith(MockitoExtension.class)
public class CustomControllerTest {

    /**
     * The controller that is being tested.
     */
    @InjectMocks
    private CustomController customController;

    /**
     * The service that the controller depends on.
     * It is mocked so that its behavior can be controlled in the tests.
     */
    @Mock
    private CustomRecipeService customRecipeService;

    /**
     * This method is run before each test.
     * It sets up the mocks for the test.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void searchRecipeWithIngredientsReturnsExpectedResultsWhenAllParametersAreProvided() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO ingredientDTO = new IngredientDTO();
        List<RecipeWithIngredientsDTO> expected = List.of(new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)));

        // When
        when(customRecipeService.searchRecipeWithIngredients("name", true, 2, List.of("ingredient"), "instructions")).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients(
            "name",
            true,
            2,
            List.of("ingredient"),
            "instructions"
        );
        assertEquals(expected, actual);
    }

    @Test
    public void searchRecipeWithIngredientsReturnsEmptyListWhenNoParametersAreProvided() {
        // Given
        List<RecipeWithIngredientsDTO> expected = List.of();

        // When
        when(customRecipeService.searchRecipeWithIngredients(null, null, null, null, null)).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients(null, null, null, null, null);
        assertEquals(expected, actual);
    }

    @Test
    public void searchRecipeWithIngredientsReturnsExpectedResultsWhenOnlyNameIsProvided() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO ingredientDTO = new IngredientDTO();
        List<RecipeWithIngredientsDTO> expected = List.of(new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)));

        // When
        when(customRecipeService.searchRecipeWithIngredients("name", null, null, null, null)).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients("name", null, null, null, null);
        assertEquals(expected, actual);
    }

    @Test
    public void searchRecipeWithIngredientsReturnsExpectedResultsWhenOnlyVegetarianIsProvided() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO ingredientDTO = new IngredientDTO();
        List<RecipeWithIngredientsDTO> expected = List.of(new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)));

        // When
        when(customRecipeService.searchRecipeWithIngredients(null, true, null, null, null)).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients(null, true, null, null, null);
        assertEquals(expected, actual);
    }

    @Test
    public void searchRecipeWithIngredientsReturnsExpectedResultsWhenOnlyServingsIsProvided() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO ingredientDTO = new IngredientDTO();
        List<RecipeWithIngredientsDTO> expected = List.of(new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)));

        // When
        when(customRecipeService.searchRecipeWithIngredients(null, null, 2, null, null)).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients(null, null, 2, null, null);
        assertEquals(expected, actual);
    }

    @Test
    public void searchRecipeWithIngredientsReturnsExpectedResultsWhenOnlyIngredientsAreProvided() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO ingredientDTO = new IngredientDTO();
        List<RecipeWithIngredientsDTO> expected = List.of(new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)));

        // When
        when(customRecipeService.searchRecipeWithIngredients(null, null, null, List.of("ingredient"), null)).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients(null, null, null, List.of("ingredient"), null);
        assertEquals(expected, actual);
    }

    @Test
    public void searchRecipeWithIngredientsReturnsExpectedResultsWhenOnlyInstructionsAreProvided() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO ingredientDTO = new IngredientDTO();
        List<RecipeWithIngredientsDTO> expected = List.of(new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)));

        // When
        when(customRecipeService.searchRecipeWithIngredients(null, null, null, null, "instructions")).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients(null, null, null, null, "instructions");
        assertEquals(expected, actual);
    }
}
