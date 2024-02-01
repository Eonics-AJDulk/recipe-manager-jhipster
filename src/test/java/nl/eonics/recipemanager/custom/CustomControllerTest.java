package nl.eonics.recipemanager.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This class contains unit tests for the CustomController class.
 */
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

    /**
     * This test checks that the searchRecipeWithIngredients method of the controller
     * returns the expected results when the service returns a list of recipes.
     */
    @Test
    public void searchRecipeWithIngredientsReturnsExpectedResults() {
        // Given
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO ingredientDTO = new IngredientDTO();
        List<RecipeWithIngredientsDTO> expected = List.of(new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)));

        // When
        when(customRecipeService.searchRecipeWithIngredients(any(), any(), any(), any(), any())).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients(null, null, null, null, null);
        assertEquals(expected, actual);
    }

    /**
     * This test checks that the searchRecipeWithIngredients method of the controller
     * returns an empty list when the service does not find any matching recipes.
     */
    @Test
    public void searchRecipeWithIngredientsReturnsEmptyListWhenNoMatch() {
        // Given
        List<RecipeWithIngredientsDTO> expected = List.of();

        // When
        when(customRecipeService.searchRecipeWithIngredients(any(), any(), any(), any(), any())).thenReturn(expected);

        // Then
        List<RecipeWithIngredientsDTO> actual = customController.searchRecipeWithIngredients(
            "nonexistent",
            false,
            0,
            List.of("nonexistent"),
            "nonexistent"
        );
        assertEquals(expected, actual);
    }
}
