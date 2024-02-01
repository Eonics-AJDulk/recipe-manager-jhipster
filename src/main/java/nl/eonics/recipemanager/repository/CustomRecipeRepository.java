package nl.eonics.recipemanager.repository;

import java.util.Collection;
import java.util.List;
import nl.eonics.recipemanager.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomRecipeRepository
    extends RecipeRepositoryWithBagRelationships, JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    @Query(
        "SELECT r FROM Recipe r JOIN r.ingredients i " +
        "WHERE (?1 IS NULL OR r.vegatarian = ?1) " +
        "AND (?2 IS NULL OR r.nrOfServings = ?2) " +
        "AND (?5 IS NULL OR r.instructions LIKE %?5%) " +
        "AND (?3 IS NULL OR i.name IN ?3) " +
        "AND (?4 IS NULL OR i.name NOT IN ?4)"
    )
    Collection<Recipe> searchRecipes(
        Boolean vegatarian,
        Integer nrOfServings,
        List<String> ingredientsToInclude,
        List<String> ingredientsToExclude,
        String instructionSearch
    );
}
