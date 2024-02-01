package nl.eonics.recipemanager.repository;

import java.util.List;
import java.util.Optional;
import nl.eonics.recipemanager.domain.Recipe;
import org.springframework.data.domain.Page;

public interface RecipeRepositoryWithBagRelationships {
    Optional<Recipe> fetchBagRelationships(Optional<Recipe> recipe);

    List<Recipe> fetchBagRelationships(List<Recipe> recipes);

    Page<Recipe> fetchBagRelationships(Page<Recipe> recipes);
}
