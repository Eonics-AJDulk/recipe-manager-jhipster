package nl.eonics.recipemanager.repository;

import java.util.Collection;
import java.util.List;
import nl.eonics.recipemanager.custom.specification.CustomRecipeSpecification;
import nl.eonics.recipemanager.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomRecipeRepository
    extends RecipeRepositoryWithBagRelationships, JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    default Collection<Recipe> searchRecipes(
        Boolean vegatarian,
        Integer nrOfServings,
        List<String> ingredientsToInclude,
        List<String> ingredientsToExclude,
        String instructionSearch
    ) {
        return findAll(
            new CustomRecipeSpecification(vegatarian, nrOfServings, ingredientsToInclude, ingredientsToExclude, instructionSearch)
        );
    }
}
