package nl.eonics.recipemanager.custom.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import nl.eonics.recipemanager.custom.service.CustomRecipeService;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class CustomRecipeControllerTest {

    @Mock
    private CustomRecipeService customRecipeService;

    @InjectMocks
    private CustomRecipeController customRecipeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void searchRecipesReturnsRecipesWhenFound() {
        SearchRecipeDTO searchRecipeDTO = new SearchRecipeDTO();
        RecipeDTO recipeDTO = new RecipeDTO();
        when(
            customRecipeService.searchRecipes(
                searchRecipeDTO.getVegatarian(),
                searchRecipeDTO.getNrOfServings(),
                searchRecipeDTO.getIngredientsToInclude(),
                searchRecipeDTO.getIngredientsToExclude(),
                searchRecipeDTO.getInstructionSearch()
            )
        )
            .thenReturn(List.of(recipeDTO));

        ResponseEntity<Collection<RecipeDTO>> response = customRecipeController.searchRecipes(searchRecipeDTO);

        assertEquals(ResponseEntity.ok(List.of(recipeDTO)), response);
    }

    @Test
    public void searchRecipesReturnsEmptyWhenNotFound() {
        SearchRecipeDTO searchRecipeDTO = new SearchRecipeDTO();
        when(
            customRecipeService.searchRecipes(
                searchRecipeDTO.getVegatarian(),
                searchRecipeDTO.getNrOfServings(),
                searchRecipeDTO.getIngredientsToInclude(),
                searchRecipeDTO.getIngredientsToExclude(),
                searchRecipeDTO.getInstructionSearch()
            )
        )
            .thenReturn(Collections.emptyList());

        ResponseEntity<Collection<RecipeDTO>> response = customRecipeController.searchRecipes(searchRecipeDTO);

        assertEquals(ResponseEntity.ok(Collections.emptyList()), response);
    }
}
