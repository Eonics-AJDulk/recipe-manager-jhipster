package nl.eonics.recipemanager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link nl.eonics.recipemanager.domain.Recipe} entity. This class is used
 * in {@link nl.eonics.recipemanager.web.rest.RecipeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /recipes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RecipeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter vegatarian;

    private IntegerFilter nrOfServings;

    private StringFilter name;

    private LongFilter ingredientsId;

    private Boolean distinct;

    public RecipeCriteria() {}

    public RecipeCriteria(RecipeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.vegatarian = other.vegatarian == null ? null : other.vegatarian.copy();
        this.nrOfServings = other.nrOfServings == null ? null : other.nrOfServings.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.ingredientsId = other.ingredientsId == null ? null : other.ingredientsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RecipeCriteria copy() {
        return new RecipeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BooleanFilter getVegatarian() {
        return vegatarian;
    }

    public BooleanFilter vegatarian() {
        if (vegatarian == null) {
            vegatarian = new BooleanFilter();
        }
        return vegatarian;
    }

    public void setVegatarian(BooleanFilter vegatarian) {
        this.vegatarian = vegatarian;
    }

    public IntegerFilter getNrOfServings() {
        return nrOfServings;
    }

    public IntegerFilter nrOfServings() {
        if (nrOfServings == null) {
            nrOfServings = new IntegerFilter();
        }
        return nrOfServings;
    }

    public void setNrOfServings(IntegerFilter nrOfServings) {
        this.nrOfServings = nrOfServings;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getIngredientsId() {
        return ingredientsId;
    }

    public LongFilter ingredientsId() {
        if (ingredientsId == null) {
            ingredientsId = new LongFilter();
        }
        return ingredientsId;
    }

    public void setIngredientsId(LongFilter ingredientsId) {
        this.ingredientsId = ingredientsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RecipeCriteria that = (RecipeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(vegatarian, that.vegatarian) &&
            Objects.equals(nrOfServings, that.nrOfServings) &&
            Objects.equals(name, that.name) &&
            Objects.equals(ingredientsId, that.ingredientsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vegatarian, nrOfServings, name, ingredientsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (vegatarian != null ? "vegatarian=" + vegatarian + ", " : "") +
            (nrOfServings != null ? "nrOfServings=" + nrOfServings + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (ingredientsId != null ? "ingredientsId=" + ingredientsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
