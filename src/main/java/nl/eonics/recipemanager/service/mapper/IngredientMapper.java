package nl.eonics.recipemanager.service.mapper;

import nl.eonics.recipemanager.domain.Ingredient;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ingredient} and its DTO {@link IngredientDTO}.
 */
@Mapper(componentModel = "spring")
public interface IngredientMapper extends EntityMapper<IngredientDTO, Ingredient> {}
