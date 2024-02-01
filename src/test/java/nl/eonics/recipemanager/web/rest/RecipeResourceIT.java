package nl.eonics.recipemanager.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import nl.eonics.recipemanager.IntegrationTest;
import nl.eonics.recipemanager.domain.Ingredient;
import nl.eonics.recipemanager.domain.Recipe;
import nl.eonics.recipemanager.repository.RecipeRepository;
import nl.eonics.recipemanager.repository.search.RecipeSearchRepository;
import nl.eonics.recipemanager.service.RecipeService;
import nl.eonics.recipemanager.service.criteria.RecipeCriteria;
import nl.eonics.recipemanager.service.dto.RecipeDTO;
import nl.eonics.recipemanager.service.mapper.RecipeMapper;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link RecipeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RecipeResourceIT {

    private static final Boolean DEFAULT_VEGATARIAN = false;
    private static final Boolean UPDATED_VEGATARIAN = true;

    private static final Integer DEFAULT_NR_OF_SERVINGS = 1;
    private static final Integer UPDATED_NR_OF_SERVINGS = 2;
    private static final Integer SMALLER_NR_OF_SERVINGS = 1 - 1;

    private static final String DEFAULT_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_INSTRUCTIONS = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/recipes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/recipes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeRepository recipeRepositoryMock;

    @Autowired
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeService recipeServiceMock;

    @Autowired
    private RecipeSearchRepository recipeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createEntity(EntityManager em) {
        Recipe recipe = new Recipe()
            .vegatarian(DEFAULT_VEGATARIAN)
            .nrOfServings(DEFAULT_NR_OF_SERVINGS)
            .instructions(DEFAULT_INSTRUCTIONS)
            .name(DEFAULT_NAME);
        return recipe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createUpdatedEntity(EntityManager em) {
        Recipe recipe = new Recipe()
            .vegatarian(UPDATED_VEGATARIAN)
            .nrOfServings(UPDATED_NR_OF_SERVINGS)
            .instructions(UPDATED_INSTRUCTIONS)
            .name(UPDATED_NAME);
        return recipe;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        recipeSearchRepository.deleteAll();
        assertThat(recipeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        recipe = createEntity(em);
    }

    @Test
    @Transactional
    void createRecipe() throws Exception {
        int databaseSizeBeforeCreate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);
        restRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
            .andExpect(status().isCreated());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getVegatarian()).isEqualTo(DEFAULT_VEGATARIAN);
        assertThat(testRecipe.getNrOfServings()).isEqualTo(DEFAULT_NR_OF_SERVINGS);
        assertThat(testRecipe.getInstructions()).isEqualTo(DEFAULT_INSTRUCTIONS);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createRecipeWithExistingId() throws Exception {
        // Create the Recipe with an existing ID
        recipe.setId(1L);
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        int databaseSizeBeforeCreate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRecipes() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].vegatarian").value(hasItem(DEFAULT_VEGATARIAN.booleanValue())))
            .andExpect(jsonPath("$.[*].nrOfServings").value(hasItem(DEFAULT_NR_OF_SERVINGS)))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRecipesWithEagerRelationshipsIsEnabled() throws Exception {
        when(recipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRecipeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(recipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRecipesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(recipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRecipeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(recipeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get the recipe
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL_ID, recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipe.getId().intValue()))
            .andExpect(jsonPath("$.vegatarian").value(DEFAULT_VEGATARIAN.booleanValue()))
            .andExpect(jsonPath("$.nrOfServings").value(DEFAULT_NR_OF_SERVINGS))
            .andExpect(jsonPath("$.instructions").value(DEFAULT_INSTRUCTIONS.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getRecipesByIdFiltering() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        Long id = recipe.getId();

        defaultRecipeShouldBeFound("id.equals=" + id);
        defaultRecipeShouldNotBeFound("id.notEquals=" + id);

        defaultRecipeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRecipeShouldNotBeFound("id.greaterThan=" + id);

        defaultRecipeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRecipeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRecipesByVegatarianIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where vegatarian equals to DEFAULT_VEGATARIAN
        defaultRecipeShouldBeFound("vegatarian.equals=" + DEFAULT_VEGATARIAN);

        // Get all the recipeList where vegatarian equals to UPDATED_VEGATARIAN
        defaultRecipeShouldNotBeFound("vegatarian.equals=" + UPDATED_VEGATARIAN);
    }

    @Test
    @Transactional
    void getAllRecipesByVegatarianIsInShouldWork() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where vegatarian in DEFAULT_VEGATARIAN or UPDATED_VEGATARIAN
        defaultRecipeShouldBeFound("vegatarian.in=" + DEFAULT_VEGATARIAN + "," + UPDATED_VEGATARIAN);

        // Get all the recipeList where vegatarian equals to UPDATED_VEGATARIAN
        defaultRecipeShouldNotBeFound("vegatarian.in=" + UPDATED_VEGATARIAN);
    }

    @Test
    @Transactional
    void getAllRecipesByVegatarianIsNullOrNotNull() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where vegatarian is not null
        defaultRecipeShouldBeFound("vegatarian.specified=true");

        // Get all the recipeList where vegatarian is null
        defaultRecipeShouldNotBeFound("vegatarian.specified=false");
    }

    @Test
    @Transactional
    void getAllRecipesByNrOfServingsIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where nrOfServings equals to DEFAULT_NR_OF_SERVINGS
        defaultRecipeShouldBeFound("nrOfServings.equals=" + DEFAULT_NR_OF_SERVINGS);

        // Get all the recipeList where nrOfServings equals to UPDATED_NR_OF_SERVINGS
        defaultRecipeShouldNotBeFound("nrOfServings.equals=" + UPDATED_NR_OF_SERVINGS);
    }

    @Test
    @Transactional
    void getAllRecipesByNrOfServingsIsInShouldWork() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where nrOfServings in DEFAULT_NR_OF_SERVINGS or UPDATED_NR_OF_SERVINGS
        defaultRecipeShouldBeFound("nrOfServings.in=" + DEFAULT_NR_OF_SERVINGS + "," + UPDATED_NR_OF_SERVINGS);

        // Get all the recipeList where nrOfServings equals to UPDATED_NR_OF_SERVINGS
        defaultRecipeShouldNotBeFound("nrOfServings.in=" + UPDATED_NR_OF_SERVINGS);
    }

    @Test
    @Transactional
    void getAllRecipesByNrOfServingsIsNullOrNotNull() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where nrOfServings is not null
        defaultRecipeShouldBeFound("nrOfServings.specified=true");

        // Get all the recipeList where nrOfServings is null
        defaultRecipeShouldNotBeFound("nrOfServings.specified=false");
    }

    @Test
    @Transactional
    void getAllRecipesByNrOfServingsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where nrOfServings is greater than or equal to DEFAULT_NR_OF_SERVINGS
        defaultRecipeShouldBeFound("nrOfServings.greaterThanOrEqual=" + DEFAULT_NR_OF_SERVINGS);

        // Get all the recipeList where nrOfServings is greater than or equal to UPDATED_NR_OF_SERVINGS
        defaultRecipeShouldNotBeFound("nrOfServings.greaterThanOrEqual=" + UPDATED_NR_OF_SERVINGS);
    }

    @Test
    @Transactional
    void getAllRecipesByNrOfServingsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where nrOfServings is less than or equal to DEFAULT_NR_OF_SERVINGS
        defaultRecipeShouldBeFound("nrOfServings.lessThanOrEqual=" + DEFAULT_NR_OF_SERVINGS);

        // Get all the recipeList where nrOfServings is less than or equal to SMALLER_NR_OF_SERVINGS
        defaultRecipeShouldNotBeFound("nrOfServings.lessThanOrEqual=" + SMALLER_NR_OF_SERVINGS);
    }

    @Test
    @Transactional
    void getAllRecipesByNrOfServingsIsLessThanSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where nrOfServings is less than DEFAULT_NR_OF_SERVINGS
        defaultRecipeShouldNotBeFound("nrOfServings.lessThan=" + DEFAULT_NR_OF_SERVINGS);

        // Get all the recipeList where nrOfServings is less than UPDATED_NR_OF_SERVINGS
        defaultRecipeShouldBeFound("nrOfServings.lessThan=" + UPDATED_NR_OF_SERVINGS);
    }

    @Test
    @Transactional
    void getAllRecipesByNrOfServingsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where nrOfServings is greater than DEFAULT_NR_OF_SERVINGS
        defaultRecipeShouldNotBeFound("nrOfServings.greaterThan=" + DEFAULT_NR_OF_SERVINGS);

        // Get all the recipeList where nrOfServings is greater than SMALLER_NR_OF_SERVINGS
        defaultRecipeShouldBeFound("nrOfServings.greaterThan=" + SMALLER_NR_OF_SERVINGS);
    }

    @Test
    @Transactional
    void getAllRecipesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name equals to DEFAULT_NAME
        defaultRecipeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the recipeList where name equals to UPDATED_NAME
        defaultRecipeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRecipeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the recipeList where name equals to UPDATED_NAME
        defaultRecipeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name is not null
        defaultRecipeShouldBeFound("name.specified=true");

        // Get all the recipeList where name is null
        defaultRecipeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllRecipesByNameContainsSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name contains DEFAULT_NAME
        defaultRecipeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the recipeList where name contains UPDATED_NAME
        defaultRecipeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name does not contain DEFAULT_NAME
        defaultRecipeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the recipeList where name does not contain UPDATED_NAME
        defaultRecipeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByIngredientsIsEqualToSomething() throws Exception {
        Ingredient ingredients;
        if (TestUtil.findAll(em, Ingredient.class).isEmpty()) {
            recipeRepository.saveAndFlush(recipe);
            ingredients = IngredientResourceIT.createEntity(em);
        } else {
            ingredients = TestUtil.findAll(em, Ingredient.class).get(0);
        }
        em.persist(ingredients);
        em.flush();
        recipe.addIngredients(ingredients);
        recipeRepository.saveAndFlush(recipe);
        Long ingredientsId = ingredients.getId();

        // Get all the recipeList where ingredients equals to ingredientsId
        defaultRecipeShouldBeFound("ingredientsId.equals=" + ingredientsId);

        // Get all the recipeList where ingredients equals to (ingredientsId + 1)
        defaultRecipeShouldNotBeFound("ingredientsId.equals=" + (ingredientsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRecipeShouldBeFound(String filter) throws Exception {
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].vegatarian").value(hasItem(DEFAULT_VEGATARIAN.booleanValue())))
            .andExpect(jsonPath("$.[*].nrOfServings").value(hasItem(DEFAULT_NR_OF_SERVINGS)))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRecipeShouldNotBeFound(String filter) throws Exception {
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRecipe() throws Exception {
        // Get the recipe
        restRecipeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipeSearchRepository.save(recipe);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());

        // Update the recipe
        Recipe updatedRecipe = recipeRepository.findById(recipe.getId()).get();
        // Disconnect from session so that the updates on updatedRecipe are not directly saved in db
        em.detach(updatedRecipe);
        updatedRecipe
            .vegatarian(UPDATED_VEGATARIAN)
            .nrOfServings(UPDATED_NR_OF_SERVINGS)
            .instructions(UPDATED_INSTRUCTIONS)
            .name(UPDATED_NAME);
        RecipeDTO recipeDTO = recipeMapper.toDto(updatedRecipe);

        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recipeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getVegatarian()).isEqualTo(UPDATED_VEGATARIAN);
        assertThat(testRecipe.getNrOfServings()).isEqualTo(UPDATED_NR_OF_SERVINGS);
        assertThat(testRecipe.getInstructions()).isEqualTo(UPDATED_INSTRUCTIONS);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Recipe> recipeSearchList = IterableUtils.toList(recipeSearchRepository.findAll());
                Recipe testRecipeSearch = recipeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRecipeSearch.getVegatarian()).isEqualTo(UPDATED_VEGATARIAN);
                assertThat(testRecipeSearch.getNrOfServings()).isEqualTo(UPDATED_NR_OF_SERVINGS);
                assertThat(testRecipeSearch.getInstructions()).isEqualTo(UPDATED_INSTRUCTIONS);
                assertThat(testRecipeSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recipeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRecipeWithPatch() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe using partial update
        Recipe partialUpdatedRecipe = new Recipe();
        partialUpdatedRecipe.setId(recipe.getId());

        partialUpdatedRecipe.vegatarian(UPDATED_VEGATARIAN).nrOfServings(UPDATED_NR_OF_SERVINGS);

        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipe))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getVegatarian()).isEqualTo(UPDATED_VEGATARIAN);
        assertThat(testRecipe.getNrOfServings()).isEqualTo(UPDATED_NR_OF_SERVINGS);
        assertThat(testRecipe.getInstructions()).isEqualTo(DEFAULT_INSTRUCTIONS);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateRecipeWithPatch() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe using partial update
        Recipe partialUpdatedRecipe = new Recipe();
        partialUpdatedRecipe.setId(recipe.getId());

        partialUpdatedRecipe
            .vegatarian(UPDATED_VEGATARIAN)
            .nrOfServings(UPDATED_NR_OF_SERVINGS)
            .instructions(UPDATED_INSTRUCTIONS)
            .name(UPDATED_NAME);

        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipe))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getVegatarian()).isEqualTo(UPDATED_VEGATARIAN);
        assertThat(testRecipe.getNrOfServings()).isEqualTo(UPDATED_NR_OF_SERVINGS);
        assertThat(testRecipe.getInstructions()).isEqualTo(UPDATED_INSTRUCTIONS);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recipeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(recipeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);
        recipeRepository.save(recipe);
        recipeSearchRepository.save(recipe);

        int databaseSizeBeforeDelete = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the recipe
        restRecipeMockMvc
            .perform(delete(ENTITY_API_URL_ID, recipe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRecipe() throws Exception {
        // Initialize the database
        recipe = recipeRepository.saveAndFlush(recipe);
        recipeSearchRepository.save(recipe);

        // Search the recipe
        restRecipeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].vegatarian").value(hasItem(DEFAULT_VEGATARIAN.booleanValue())))
            .andExpect(jsonPath("$.[*].nrOfServings").value(hasItem(DEFAULT_NR_OF_SERVINGS)))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
