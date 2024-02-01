package nl.eonics.recipemanager.custom.web.rest;

import java.util.List;
import nl.eonics.recipemanager.custom.service.CustomRecipeService;
import nl.eonics.recipemanager.custom.service.dto.RecipeWithIngredientsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * This class is a Rest Controller for handling requests related to custom recipes.
 * It is mapped to the "/api/search" URL path.
 */
@RestController
@RequestMapping("/api/search")
public class CustomController {

    private final Logger log = LoggerFactory.getLogger(CustomController.class);

    // The service that this controller will use to perform operations.
    private final CustomRecipeService customRecipeService;

    /**
     * Constructs a new CustomController with the given CustomRecipeService.
     *
     * @param customRecipeService the service to be used by this controller
     */
    public CustomController(CustomRecipeService customRecipeService) {
        log.debug("Constructing CustomController");
        this.customRecipeService = customRecipeService;
    }

    /**
     * Handles GET requests to "/recipe-with-ingredients".
     * Returns a list of RecipeWithIngredientsDTO that match the given parameters.
     *
     * @param name         the name of the recipe (optional)
     * @param vegetarian   whether the recipe is vegetarian (optional)
     * @param servings     the number of servings the recipe makes (optional)
     * @param ingredients  the ingredients used in the recipe (optional)
     * @param instructions the instructions for the recipe (optional)
     * @return a list of RecipeWithIngredientsDTO that match the given parameters
     */
    @GetMapping("/recipe-with-ingredients")
    public List<RecipeWithIngredientsDTO> searchRecipeWithIngredients(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean vegetarian,
        @RequestParam(required = false) Integer servings,
        @RequestParam(required = false) List<String> ingredients,
        @RequestParam(required = false) String instructions
    ) {
        log.debug("Handling GET request to /recipe-with-ingredients");
        return customRecipeService.searchRecipeWithIngredients(name, vegetarian, servings, ingredients, instructions);
    }
}
