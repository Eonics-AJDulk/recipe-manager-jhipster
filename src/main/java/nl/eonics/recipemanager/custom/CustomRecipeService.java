package nl.eonics.recipemanager.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nl.eonics.recipemanager.service.IngredientQueryService;
import nl.eonics.recipemanager.service.RecipeQueryService;
import nl.eonics.recipemanager.service.criteria.IngredientCriteria;
import nl.eonics.recipemanager.service.criteria.RecipeCriteria;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.StringFilter;

@Service
public class CustomRecipeService {

    @Autowired
    private RecipeQueryService recipeQueryService;

    @Autowired
    private IngredientQueryService ingredientQueryService;

    public List<RecipeWithIngredientsDTO> searchRecipeWithIngredients(
        String name,
        Boolean vegetarian,
        Integer servings,
        List<String> ingredients,
        String instructions
    ) {
        List<RecipeWithIngredientsDTO> recipes = new ArrayList<>();

        if (ingredients != null && !ingredients.isEmpty()) {
            for (String ingredient : ingredients) {
                IngredientCriteria ingredientCriteria = new IngredientCriteria();
                StringFilter stringFilter = new StringFilter();
                stringFilter.setEquals(ingredient);
                ingredientCriteria.setName(stringFilter);
                List<IngredientDTO> ingredientDTOs = ingredientQueryService.findByCriteria(ingredientCriteria);
                RecipeCriteria recipeCriteria = new RecipeCriteria();
                for (IngredientDTO ingredientDTO : ingredientDTOs) {
                    StringFilter recipeStringFilter = new StringFilter();
                    recipeStringFilter.setEquals(ingredientDTO.getName());
                    recipeCriteria.setName(recipeStringFilter);
                    List<RecipeDTO> recipeDTOs = recipeQueryService.findByCriteria(recipeCriteria);
                    for (RecipeDTO recipeDTO : recipeDTOs) {
                        RecipeWithIngredientsDTO recipeWithIngredientsDTO = new RecipeWithIngredientsDTO(recipeDTO, ingredientDTOs);
                        recipes.add(recipeWithIngredientsDTO);
                    }
                }
            }
        }

        recipes = filterRecipesByParameters(recipes, name, vegetarian, servings, instructions);

        return recipes;
    }

    private List<RecipeWithIngredientsDTO> filterRecipesByParameters(
        List<RecipeWithIngredientsDTO> recipes,
        String name,
        Boolean vegetarian,
        Integer servings,
        String instructions
    ) {
        // Filter the recipes based on the parameters
        return recipes
            .stream()
            .filter(recipe -> name == null || recipe.getRecipe().getName().equals(name))
            .filter(recipe -> vegetarian == null || recipe.getRecipe().getVegatarian() == vegetarian)
            .filter(recipe -> servings == null || Objects.equals(recipe.getRecipe().getNrOfServings(), servings))
            .collect(Collectors.toList());
    }
}
