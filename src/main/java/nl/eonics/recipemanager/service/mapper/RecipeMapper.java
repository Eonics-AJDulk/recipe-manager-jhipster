package nl.eonics.recipemanager.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import nl.eonics.recipemanager.domain.Ingredient;
import nl.eonics.recipemanager.domain.Recipe;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Recipe} and its DTO {@link RecipeDTO}.
 */
@Mapper(componentModel = "spring")
public interface RecipeMapper extends EntityMapper<RecipeDTO, Recipe> {
    @Mapping(target = "ingredients", source = "ingredients", qualifiedByName = "ingredientNameSet")
    RecipeDTO toDto(Recipe s);

    @Mapping(target = "removeIngredients", ignore = true)
    Recipe toEntity(RecipeDTO recipeDTO);

    @Named("ingredientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    IngredientDTO toDtoIngredientName(Ingredient ingredient);

    @Named("ingredientNameSet")
    default Set<IngredientDTO> toDtoIngredientNameSet(Set<Ingredient> ingredient) {
        return ingredient.stream().map(this::toDtoIngredientName).collect(Collectors.toSet());
    }
}
