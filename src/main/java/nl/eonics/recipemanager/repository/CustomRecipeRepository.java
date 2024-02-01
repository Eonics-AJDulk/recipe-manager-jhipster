package nl.eonics.recipemanager.repository;

import java.util.Collection;
import java.util.List;
import nl.eonics.recipemanager.domain.Recipe;
import nl.eonics.recipemanager.repository.RecipeRepositoryWithBagRelationships;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomRecipeRepository
    extends RecipeRepositoryWithBagRelationships, JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    @Query(
        "SELECT r FROM Recipe r JOIN r.ingredients i " +
        "WHERE r.vegatarian = ?1 AND r.nrOfServings = ?2 AND r.instructions LIKE %?5% " +
        "AND i.name IN ?3 " +
        "AND i.name NOT IN ?4"
    )
    Collection<Recipe> searchRecipes(
        Boolean vegatarian,
        Integer nrOfServings,
        List<String> ingredientsToInclude,
        List<String> ingredientsToExclude,
        String instructionSearch
    );
}
