package nl.eonics.recipemanager.custom;

import java.util.List;
import java.util.stream.Collectors;
import nl.eonics.recipemanager.service.IngredientQueryService;
import nl.eonics.recipemanager.service.RecipeQueryService;
import nl.eonics.recipemanager.service.criteria.IngredientCriteria;
import nl.eonics.recipemanager.service.criteria.RecipeCriteria;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.StringFilter;

/**
 * This class is a Service for handling operations related to custom recipes.
 */
@Service
public class CustomRecipeService {

    // The service that this service will use to perform operations related to recipes.
    private final RecipeQueryService recipeQueryService;
    // The service that this service will use to perform operations related to ingredients.
    private final IngredientQueryService ingredientQueryService;

    /**
     * Constructs a new CustomRecipeService with the given RecipeQueryService and IngredientQueryService.
     *
     * @param recipeQueryService     the service to be used by this service for operations related to recipes
     * @param ingredientQueryService the service to be used by this service for operations related to ingredients
     */
    public CustomRecipeService(RecipeQueryService recipeQueryService, IngredientQueryService ingredientQueryService) {
        this.recipeQueryService = recipeQueryService;
        this.ingredientQueryService = ingredientQueryService;
    }

    /**
     * Searches for recipes with ingredients that match the given parameters.
     *
     * @param name         the name of the recipe (optional)
     * @param vegetarian   whether the recipe is vegetarian (optional)
     * @param servings     the number of servings the recipe makes (optional)
     * @param ingredients  the ingredients used in the recipe (optional)
     * @param instructions the instructions for the recipe (optional)
     * @return a list of RecipeWithIngredientsDTO that match the given parameters
     */
    public List<RecipeWithIngredientsDTO> searchRecipeWithIngredients(
        String name,
        Boolean vegetarian,
        Integer servings,
        List<String> ingredients,
        String instructions
    ) {
        return ingredients == null || ingredients.isEmpty()
            ? List.of()
            : ingredients
                .stream()
                .flatMap(ingredient -> getIngredientDTOs(ingredient).stream())
                .flatMap(ingredientDTO -> getRecipeWithIngredientsDTOs(ingredientDTO).stream())
                .filter(recipe -> filterByParameters(recipe, name, vegetarian, servings, instructions))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of IngredientDTO that match the given ingredient name.
     *
     * @param ingredient the name of the ingredient
     * @return a list of IngredientDTO that match the given ingredient name
     */
    private List<IngredientDTO> getIngredientDTOs(String ingredient) {
        IngredientCriteria ingredientCriteria = new IngredientCriteria();
        ingredientCriteria.setName(createStringFilter(ingredient));
        return ingredientQueryService.findByCriteria(ingredientCriteria);
    }

    /**
     * Gets a list of RecipeWithIngredientsDTO that match the given IngredientDTO name.
     *
     * @param ingredientDTO the IngredientDTO to match
     * @return a list of RecipeWithIngredientsDTO that match the given IngredientDTO name
     */
    private List<RecipeWithIngredientsDTO> getRecipeWithIngredientsDTOs(IngredientDTO ingredientDTO) {
        RecipeCriteria recipeCriteria = new RecipeCriteria();
        recipeCriteria.setName(createStringFilter(ingredientDTO.getName()));
        return recipeQueryService
            .findByCriteria(recipeCriteria)
            .stream()
            .map(recipeDTO -> new RecipeWithIngredientsDTO(recipeDTO, List.of(ingredientDTO)))
            .collect(Collectors.toList());
    }

    /**
     * Filters a RecipeWithIngredientsDTO by the given parameters.
     *
     * @param recipe       the RecipeWithIngredientsDTO to filter
     * @param name         the name of the recipe (optional)
     * @param vegetarian   whether the recipe is vegetarian (optional)
     * @param servings     the number of servings the recipe makes (optional)
     * @param instructions the instructions for the recipe (optional)
     * @return true if the RecipeWithIngredientsDTO matches the given parameters, false otherwise
     */
    private boolean filterByParameters(
        RecipeWithIngredientsDTO recipe,
        String name,
        Boolean vegetarian,
        Integer servings,
        String instructions
    ) {
        return (
            (name == null || recipe.getRecipe().getName().equals(name)) &&
            (vegetarian == null || recipe.getRecipe().getVegatarian() == vegetarian) &&
            (servings == null || servings.equals(recipe.getRecipe().getNrOfServings())) &&
            (instructions == null || recipe.getRecipe().getInstructions().contains(instructions))
        );
    }

    /**
     * Creates a StringFilter with the given value.
     *
     * @param value the value to set in the StringFilter
     * @return a StringFilter with the given value
     */
    private StringFilter createStringFilter(String value) {
        StringFilter stringFilter = new StringFilter();
        stringFilter.setEquals(value);
        return stringFilter;
    }
}
