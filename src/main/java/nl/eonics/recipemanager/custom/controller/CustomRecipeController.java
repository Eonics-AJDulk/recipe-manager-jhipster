package nl.eonics.recipemanager.custom.controller;

import java.util.Collection;
import java.util.List;
import nl.eonics.recipemanager.custom.service.CustomRecipeService;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CustomRecipeController {

    private final CustomRecipeService customRecipeService;

    public CustomRecipeController(CustomRecipeService customRecipeService) {
        this.customRecipeService = customRecipeService;
    }

    @PostMapping("/recipe/search")
    public ResponseEntity<Collection<RecipeDTO>> searchRecipes(@RequestBody SearchRecipeDTO searchRecipeDTO) {
        return ResponseEntity.ok(
            customRecipeService.searchRecipes(
                searchRecipeDTO.getVegatarian(),
                searchRecipeDTO.getNrOfServings(),
                searchRecipeDTO.getIngredientsToInclude(),
                searchRecipeDTO.getIngredientsToExclude(),
                searchRecipeDTO.getInstructionSearch()
            )
        );
    }
}
