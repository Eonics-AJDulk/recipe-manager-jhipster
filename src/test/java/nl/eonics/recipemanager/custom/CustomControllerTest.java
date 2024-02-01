package nl.eonics.recipemanager.custom;

import java.util.List;
import nl.eonics.recipemanager.service.IngredientQueryService;
import nl.eonics.recipemanager.service.RecipeQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

public class CustomControllerTest {

    private final Logger log = LoggerFactory.getLogger(CustomControllerTest.class);

    @Autowired
    private RecipeQueryService recipeQueryService;

    @Autowired
    private IngredientQueryService ingredientQueryService;
}
