package nl.eonics.recipemanager.service.mapper;

import nl.eonics.recipemanager.domain.Ingredient;
import nl.eonics.recipemanager.domain.Recipe;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ingredient} and its DTO {@link IngredientDTO}.
 */
@Mapper(componentModel = "spring")
public interface IngredientMapper extends EntityMapper<IngredientDTO, Ingredient> {
    @Mapping(target = "recipe", source = "recipe", qualifiedByName = "recipeName")
    IngredientDTO toDto(Ingredient s);

    @Named("recipeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RecipeDTO toDtoRecipeName(Recipe recipe);
}
