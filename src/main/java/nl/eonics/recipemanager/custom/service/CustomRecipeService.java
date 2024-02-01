package nl.eonics.recipemanager.custom.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import nl.eonics.recipemanager.repository.CustomRecipeRepository;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import nl.eonics.recipemanager.service.mapper.RecipeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomRecipeService {

    private final CustomRecipeRepository customRecipeRepository;

    private final RecipeMapper recipeMapper;

    public CustomRecipeService(CustomRecipeRepository customRecipeRepository, RecipeMapper recipeMapper) {
        this.customRecipeRepository = customRecipeRepository;
        this.recipeMapper = recipeMapper;
    }

    public Collection<RecipeDTO> searchRecipes(
        Boolean vegatarian,
        Integer nrOfServings,
        List<String> ingredientsToInclude,
        List<String> ingredientsToExclude,
        String instructionSearch
    ) {
        return this.customRecipeRepository.searchRecipes(
                vegatarian,
                nrOfServings,
                ingredientsToInclude,
                ingredientsToExclude,
                instructionSearch
            )
            .stream()
            .map(recipeMapper::toDto)
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
