package nl.eonics.recipemanager.custom;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class CustomController {

    private final Logger log = LoggerFactory.getLogger(CustomController.class);

    @Autowired
    private CustomRecipeService customRecipeService;

    @GetMapping("/recipe-with-ingredients")
    public ResponseEntity<List<RecipeWithIngredientsDTO>> searchRecipeWithIngredients(
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "vegetarian", required = false) Boolean vegetarian,
        @RequestParam(name = "servings", required = false) Integer servings,
        @RequestParam(name = "ingredient", required = false) List<String> ingredients,
        @RequestParam(name = "instructions", required = false) String instructions
    ) {
        log.debug("REST request to search for full recipes");

        List<RecipeWithIngredientsDTO> recipes = customRecipeService.searchRecipeWithIngredients(
            name,
            vegetarian,
            servings,
            ingredients,
            instructions
        );

        return ResponseEntity.ok(recipes);
    }
}
