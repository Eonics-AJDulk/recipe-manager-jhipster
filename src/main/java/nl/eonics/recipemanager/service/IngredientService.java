package nl.eonics.recipemanager.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import nl.eonics.recipemanager.domain.Ingredient;
import nl.eonics.recipemanager.repository.IngredientRepository;
import nl.eonics.recipemanager.repository.search.IngredientSearchRepository;
import nl.eonics.recipemanager.service.dto.IngredientDTO;
import nl.eonics.recipemanager.service.mapper.IngredientMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Ingredient}.
 */
@Service
@Transactional
public class IngredientService {

    private final Logger log = LoggerFactory.getLogger(IngredientService.class);

    private final IngredientRepository ingredientRepository;

    private final IngredientMapper ingredientMapper;

    private final IngredientSearchRepository ingredientSearchRepository;

    public IngredientService(
        IngredientRepository ingredientRepository,
        IngredientMapper ingredientMapper,
        IngredientSearchRepository ingredientSearchRepository
    ) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
        this.ingredientSearchRepository = ingredientSearchRepository;
    }

    /**
     * Save a ingredient.
     *
     * @param ingredientDTO the entity to save.
     * @return the persisted entity.
     */
    public IngredientDTO save(IngredientDTO ingredientDTO) {
        log.debug("Request to save Ingredient : {}", ingredientDTO);
        Ingredient ingredient = ingredientMapper.toEntity(ingredientDTO);
        ingredient = ingredientRepository.save(ingredient);
        IngredientDTO result = ingredientMapper.toDto(ingredient);
        ingredientSearchRepository.index(ingredient);
        return result;
    }

    /**
     * Update a ingredient.
     *
     * @param ingredientDTO the entity to save.
     * @return the persisted entity.
     */
    public IngredientDTO update(IngredientDTO ingredientDTO) {
        log.debug("Request to update Ingredient : {}", ingredientDTO);
        Ingredient ingredient = ingredientMapper.toEntity(ingredientDTO);
        ingredient = ingredientRepository.save(ingredient);
        IngredientDTO result = ingredientMapper.toDto(ingredient);
        ingredientSearchRepository.index(ingredient);
        return result;
    }

    /**
     * Partially update a ingredient.
     *
     * @param ingredientDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IngredientDTO> partialUpdate(IngredientDTO ingredientDTO) {
        log.debug("Request to partially update Ingredient : {}", ingredientDTO);

        return ingredientRepository
            .findById(ingredientDTO.getId())
            .map(existingIngredient -> {
                ingredientMapper.partialUpdate(existingIngredient, ingredientDTO);

                return existingIngredient;
            })
            .map(ingredientRepository::save)
            .map(savedIngredient -> {
                ingredientSearchRepository.save(savedIngredient);

                return savedIngredient;
            })
            .map(ingredientMapper::toDto);
    }

    /**
     * Get all the ingredients.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<IngredientDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Ingredients");
        return ingredientRepository.findAll(pageable).map(ingredientMapper::toDto);
    }

    /**
     * Get all the ingredients with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<IngredientDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ingredientRepository.findAllWithEagerRelationships(pageable).map(ingredientMapper::toDto);
    }

    /**
     * Get one ingredient by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IngredientDTO> findOne(Long id) {
        log.debug("Request to get Ingredient : {}", id);
        return ingredientRepository.findOneWithEagerRelationships(id).map(ingredientMapper::toDto);
    }

    /**
     * Delete the ingredient by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Ingredient : {}", id);
        ingredientRepository.deleteById(id);
        ingredientSearchRepository.deleteById(id);
    }

    /**
     * Search for the ingredient corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<IngredientDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Ingredients for query {}", query);
        return ingredientSearchRepository.search(query, pageable).map(ingredientMapper::toDto);
    }
}
