package nl.eonics.recipemanager.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IngredientMapperTest {

    private IngredientMapper ingredientMapper;

    @BeforeEach
    public void setUp() {
        ingredientMapper = new IngredientMapperImpl();
    }
}
