package nl.eonics.recipemanager.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nl.eonics.recipemanager.domain.Recipe;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class RecipeRepositoryWithBagRelationshipsImpl implements RecipeRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Recipe> fetchBagRelationships(Optional<Recipe> recipe) {
        return recipe.map(this::fetchIngredients);
    }

    @Override
    public Page<Recipe> fetchBagRelationships(Page<Recipe> recipes) {
        return new PageImpl<>(fetchBagRelationships(recipes.getContent()), recipes.getPageable(), recipes.getTotalElements());
    }

    @Override
    public List<Recipe> fetchBagRelationships(List<Recipe> recipes) {
        return Optional.of(recipes).map(this::fetchIngredients).orElse(Collections.emptyList());
    }

    Recipe fetchIngredients(Recipe result) {
        return entityManager
            .createQuery("select recipe from Recipe recipe left join fetch recipe.ingredients where recipe is :recipe", Recipe.class)
            .setParameter("recipe", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Recipe> fetchIngredients(List<Recipe> recipes) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, recipes.size()).forEach(index -> order.put(recipes.get(index).getId(), index));
        List<Recipe> result = entityManager
            .createQuery(
                "select distinct recipe from Recipe recipe left join fetch recipe.ingredients where recipe in :recipes",
                Recipe.class
            )
            .setParameter("recipes", recipes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
