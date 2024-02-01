package nl.eonics.recipemanager.custom.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import nl.eonics.recipemanager.custom.service.CustomRecipeService;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

public class SearchRecipeDTOTest {

    @Mock
    private CustomRecipeService customRecipeService;

    @InjectMocks
    private CustomRecipeController customRecipeController;

    @Test
    public void searchRecipesHandlesNullVegatarian() {
        SearchRecipeDTO searchRecipeDTO = new SearchRecipeDTO();
        searchRecipeDTO.setVegatarian(null);
        RecipeDTO recipeDTO = new RecipeDTO();
        when(customRecipeService.searchRecipes(anyBoolean(), anyInt(), anyList(), anyList(), anyString())).thenReturn(List.of(recipeDTO));

        ResponseEntity<Collection<RecipeDTO>> response = customRecipeController.searchRecipes(searchRecipeDTO);

        assertEquals(ResponseEntity.ok(List.of(recipeDTO)), response);
    }

    @Test
    public void searchRecipesHandlesNullNrOfServings() {
        SearchRecipeDTO searchRecipeDTO = new SearchRecipeDTO();
        searchRecipeDTO.setNrOfServings(null);
        RecipeDTO recipeDTO = new RecipeDTO();
        when(
            customRecipeService.searchRecipes(
                searchRecipeDTO.getVegatarian(),
                null,
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
    public void searchRecipesHandlesEmptyIngredientsToInclude() {
        SearchRecipeDTO searchRecipeDTO = new SearchRecipeDTO();
        searchRecipeDTO.setIngredientsToInclude(Collections.emptyList());
        RecipeDTO recipeDTO = new RecipeDTO();
        when(
            customRecipeService.searchRecipes(
                searchRecipeDTO.getVegatarian(),
                searchRecipeDTO.getNrOfServings(),
                Collections.emptyList(),
                searchRecipeDTO.getIngredientsToExclude(),
                searchRecipeDTO.getInstructionSearch()
            )
        )
            .thenReturn(List.of(recipeDTO));

        ResponseEntity<Collection<RecipeDTO>> response = customRecipeController.searchRecipes(searchRecipeDTO);

        assertEquals(ResponseEntity.ok(List.of(recipeDTO)), response);
    }

    @Test
    public void searchRecipesHandlesEmptyIngredientsToExclude() {
        SearchRecipeDTO searchRecipeDTO = new SearchRecipeDTO();
        searchRecipeDTO.setIngredientsToExclude(Collections.emptyList());
        RecipeDTO recipeDTO = new RecipeDTO();
        when(
            customRecipeService.searchRecipes(
                searchRecipeDTO.getVegatarian(),
                searchRecipeDTO.getNrOfServings(),
                searchRecipeDTO.getIngredientsToInclude(),
                Collections.emptyList(),
                searchRecipeDTO.getInstructionSearch()
            )
        )
            .thenReturn(List.of(recipeDTO));

        ResponseEntity<Collection<RecipeDTO>> response = customRecipeController.searchRecipes(searchRecipeDTO);

        assertEquals(ResponseEntity.ok(List.of(recipeDTO)), response);
    }

    @Test
    public void searchRecipesHandlesNullInstructionSearch() {
        SearchRecipeDTO searchRecipeDTO = new SearchRecipeDTO();
        searchRecipeDTO.setInstructionSearch(null);
        RecipeDTO recipeDTO = new RecipeDTO();
        when(
            customRecipeService.searchRecipes(
                searchRecipeDTO.getVegatarian(),
                searchRecipeDTO.getNrOfServings(),
                searchRecipeDTO.getIngredientsToInclude(),
                searchRecipeDTO.getIngredientsToExclude(),
                null
            )
        )
            .thenReturn(List.of(recipeDTO));

        ResponseEntity<Collection<RecipeDTO>> response = customRecipeController.searchRecipes(searchRecipeDTO);

        assertEquals(ResponseEntity.ok(List.of(recipeDTO)), response);
    }
}
