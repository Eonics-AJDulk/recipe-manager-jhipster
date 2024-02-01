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
import nl.eonics.recipemanager.repository.IngredientRepository;
import nl.eonics.recipemanager.repository.search.IngredientSearchRepository;
import nl.eonics.recipemanager.service.IngredientService;
import nl.eonics.recipemanager.service.criteria.IngredientCriteria;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.mapper.IngredientMapper;
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

/**
 * Integration tests for the {@link IngredientResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IngredientResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ingredients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/ingredients";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientRepository ingredientRepositoryMock;

    @Autowired
    private IngredientMapper ingredientMapper;

    @Mock
    private IngredientService ingredientServiceMock;

    @Autowired
    private IngredientSearchRepository ingredientSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIngredientMockMvc;

    private Ingredient ingredient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ingredient createEntity(EntityManager em) {
        Ingredient ingredient = new Ingredient().name(DEFAULT_NAME);
        return ingredient;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ingredient createUpdatedEntity(EntityManager em) {
        Ingredient ingredient = new Ingredient().name(UPDATED_NAME);
        return ingredient;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        ingredientSearchRepository.deleteAll();
        assertThat(ingredientSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        ingredient = createEntity(em);
    }

    @Test
    @Transactional
    void createIngredient() throws Exception {
        int databaseSizeBeforeCreate = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        // Create the Ingredient
        IngredientDTO ingredientDTO = ingredientMapper.toDto(ingredient);
        restIngredientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ingredientDTO)))
            .andExpect(status().isCreated());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Ingredient testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createIngredientWithExistingId() throws Exception {
        // Create the Ingredient with an existing ID
        ingredient.setId(1L);
        IngredientDTO ingredientDTO = ingredientMapper.toDto(ingredient);

        int databaseSizeBeforeCreate = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restIngredientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ingredientDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllIngredients() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredient.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIngredientsWithEagerRelationshipsIsEnabled() throws Exception {
        when(ingredientServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIngredientMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ingredientServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIngredientsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ingredientServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIngredientMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ingredientRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get the ingredient
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL_ID, ingredient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ingredient.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getIngredientsByIdFiltering() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        Long id = ingredient.getId();

        defaultIngredientShouldBeFound("id.equals=" + id);
        defaultIngredientShouldNotBeFound("id.notEquals=" + id);

        defaultIngredientShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultIngredientShouldNotBeFound("id.greaterThan=" + id);

        defaultIngredientShouldBeFound("id.lessThanOrEqual=" + id);
        defaultIngredientShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name equals to DEFAULT_NAME
        defaultIngredientShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the ingredientList where name equals to UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name in DEFAULT_NAME or UPDATED_NAME
        defaultIngredientShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the ingredientList where name equals to UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name is not null
        defaultIngredientShouldBeFound("name.specified=true");

        // Get all the ingredientList where name is null
        defaultIngredientShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllIngredientsByNameContainsSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name contains DEFAULT_NAME
        defaultIngredientShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the ingredientList where name contains UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name does not contain DEFAULT_NAME
        defaultIngredientShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the ingredientList where name does not contain UPDATED_NAME
        defaultIngredientShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByRecipeIsEqualToSomething() throws Exception {
        Recipe recipe;
        if (TestUtil.findAll(em, Recipe.class).isEmpty()) {
            ingredientRepository.saveAndFlush(ingredient);
            recipe = RecipeResourceIT.createEntity(em);
        } else {
            recipe = TestUtil.findAll(em, Recipe.class).get(0);
        }
        em.persist(recipe);
        em.flush();
        ingredient.setRecipe(recipe);
        ingredientRepository.saveAndFlush(ingredient);
        Long recipeId = recipe.getId();

        // Get all the ingredientList where recipe equals to recipeId
        defaultIngredientShouldBeFound("recipeId.equals=" + recipeId);

        // Get all the ingredientList where recipe equals to (recipeId + 1)
        defaultIngredientShouldNotBeFound("recipeId.equals=" + (recipeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIngredientShouldBeFound(String filter) throws Exception {
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredient.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIngredientShouldNotBeFound(String filter) throws Exception {
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIngredient() throws Exception {
        // Get the ingredient
        restIngredientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredientSearchRepository.save(ingredient);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());

        // Update the ingredient
        Ingredient updatedIngredient = ingredientRepository.findById(ingredient.getId()).get();
        // Disconnect from session so that the updates on updatedIngredient are not directly saved in db
        em.detach(updatedIngredient);
        updatedIngredient.name(UPDATED_NAME);
        IngredientDTO ingredientDTO = ingredientMapper.toDto(updatedIngredient);

        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ingredientDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        Ingredient testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Ingredient> ingredientSearchList = IterableUtils.toList(ingredientSearchRepository.findAll());
                Ingredient testIngredientSearch = ingredientSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testIngredientSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = ingredientMapper.toDto(ingredient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ingredientDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = ingredientMapper.toDto(ingredient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = ingredientMapper.toDto(ingredient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ingredientDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateIngredientWithPatch() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient using partial update
        Ingredient partialUpdatedIngredient = new Ingredient();
        partialUpdatedIngredient.setId(ingredient.getId());

        partialUpdatedIngredient.name(UPDATED_NAME);

        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredient.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredient))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        Ingredient testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateIngredientWithPatch() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient using partial update
        Ingredient partialUpdatedIngredient = new Ingredient();
        partialUpdatedIngredient.setId(ingredient.getId());

        partialUpdatedIngredient.name(UPDATED_NAME);

        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredient.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredient))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        Ingredient testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = ingredientMapper.toDto(ingredient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ingredientDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = ingredientMapper.toDto(ingredient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = ingredientMapper.toDto(ingredient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);
        ingredientRepository.save(ingredient);
        ingredientSearchRepository.save(ingredient);

        int databaseSizeBeforeDelete = ingredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ingredient
        restIngredientMockMvc
            .perform(delete(ENTITY_API_URL_ID, ingredient.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ingredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchIngredient() throws Exception {
        // Initialize the database
        ingredient = ingredientRepository.saveAndFlush(ingredient);
        ingredientSearchRepository.save(ingredient);

        // Search the ingredient
        restIngredientMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ingredient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredient.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
