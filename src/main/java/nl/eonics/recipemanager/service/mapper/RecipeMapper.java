package nl.eonics.recipemanager.service.mapper;

import nl.eonics.recipemanager.domain.Recipe;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Recipe} and its DTO {@link RecipeDTO}.
 */
@Mapper(componentModel = "spring")
public interface RecipeMapper extends EntityMapper<RecipeDTO, Recipe> {}
