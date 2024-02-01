package nl.eonics.recipemanager.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.eonics.recipemanager.domain.Ingredient;
import nl.eonics.recipemanager.repository.IngredientRepository;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Ingredient} entity.
 */
public interface IngredientSearchRepository extends ElasticsearchRepository<Ingredient, Long>, IngredientSearchRepositoryInternal {}

interface IngredientSearchRepositoryInternal {
    Page<Ingredient> search(String query, Pageable pageable);

    Page<Ingredient> search(Query query);

    void index(Ingredient entity);
}

class IngredientSearchRepositoryInternalImpl implements IngredientSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final IngredientRepository repository;

    IngredientSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, IngredientRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Ingredient> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Ingredient> search(Query query) {
        SearchHits<Ingredient> searchHits = elasticsearchTemplate.search(query, Ingredient.class);
        List<Ingredient> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Ingredient entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
