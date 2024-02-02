package nl.eonics.recipemanager.custom.specification;

import io.micrometer.core.lang.NonNull;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.*;
import nl.eonics.recipemanager.domain.Recipe;
import org.springframework.data.jpa.domain.Specification;

public class CustomRecipeSpecification implements Specification<Recipe> {

    private final Boolean vegatarian;
    private final Integer nrOfServings;
    private final List<String> ingredientsToInclude;
    private final List<String> ingredientsToExclude;
    private final String instructionSearch;

    public CustomRecipeSpecification(
        Boolean vegatarian,
        Integer nrOfServings,
        List<String> ingredientsToInclude,
        List<String> ingredientsToExclude,
        String instructionSearch
    ) {
        this.vegatarian = vegatarian;
        this.nrOfServings = nrOfServings;
        this.ingredientsToInclude = ingredientsToInclude;
        this.ingredientsToExclude = ingredientsToExclude;
        this.instructionSearch = instructionSearch;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Recipe> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (vegatarian != null) {
            predicates.add(criteriaBuilder.equal(root.get("vegatarian"), vegatarian));
        }

        if (nrOfServings != null) {
            predicates.add(criteriaBuilder.equal(root.get("nrOfServings"), nrOfServings));
        }

        if (instructionSearch != null) {
            predicates.add(criteriaBuilder.like(root.get("instructions"), "%" + instructionSearch + "%"));
        }

        if (ingredientsToInclude != null && !ingredientsToInclude.isEmpty()) {
            predicates.add(root.join("ingredients").get("name").in(ingredientsToInclude));
        }

        if (ingredientsToExclude != null && !ingredientsToExclude.isEmpty()) {
            Subquery<Recipe> subquery = query.subquery(Recipe.class);
            Root<Recipe> subRoot = subquery.from(Recipe.class);
            subquery.select(subRoot).where(subRoot.join("ingredients").get("name").in(ingredientsToExclude));
            predicates.add(criteriaBuilder.not(root.in(subquery)));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
