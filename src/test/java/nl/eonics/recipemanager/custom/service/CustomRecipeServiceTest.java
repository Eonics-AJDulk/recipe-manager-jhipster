package nl.eonics.recipemanager.custom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.eonics.recipemanager.repository.CustomRecipeRepository;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CustomRecipeServiceTest {

    @Mock
    private CustomRecipeRepository customRecipeRepository;

    @InjectMocks
    private CustomRecipeService customRecipeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void searchRecipesReturnsEmptyWhenNotFound() {
        when(customRecipeRepository.searchRecipes(anyBoolean(), anyInt(), anyList(), anyList(), anyString()))
            .thenReturn(Collections.emptyList());

        List<RecipeDTO> response = (List<RecipeDTO>) customRecipeService.searchRecipes(
            true,
            4,
            Arrays.asList("Chicken", "Rice"),
            Collections.emptyList(),
            "Fry"
        );

        assertEquals(Collections.emptyList(), response);
    }
}
