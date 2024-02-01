package nl.eonics.recipemanager.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Lob;

/**
 * A DTO for the {@link nl.eonics.recipemanager.domain.Recipe} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RecipeDTO implements Serializable {

    private Long id;

    private Boolean vegatarian;

    private Integer nrOfServings;

    @Lob
    private String instructions;

    private String name;

    private Set<IngredientDTO> ingredients = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getVegatarian() {
        return vegatarian;
    }

    public void setVegatarian(Boolean vegatarian) {
        this.vegatarian = vegatarian;
    }

    public Integer getNrOfServings() {
        return nrOfServings;
    }

    public void setNrOfServings(Integer nrOfServings) {
        this.nrOfServings = nrOfServings;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeDTO)) {
            return false;
        }

        RecipeDTO recipeDTO = (RecipeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, recipeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeDTO{" +
            "id=" + getId() +
            ", vegatarian='" + getVegatarian() + "'" +
            ", nrOfServings=" + getNrOfServings() +
            ", instructions='" + getInstructions() + "'" +
            ", name='" + getName() + "'" +
            ", ingredients=" + getIngredients() +
            "}";
    }
}
