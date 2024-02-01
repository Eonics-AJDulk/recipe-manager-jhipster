package nl.eonics.recipemanager.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Type;

/**
 * A Recipe.
 */
@Entity
@Table(name = "recipe")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "recipe")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "vegatarian")
    private Boolean vegatarian;

    @Column(name = "nr_of_servings")
    private Integer nrOfServings;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "instructions")
    private String instructions;

    @Column(name = "name")
    private String name;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Recipe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getVegatarian() {
        return this.vegatarian;
    }

    public Recipe vegatarian(Boolean vegatarian) {
        this.setVegatarian(vegatarian);
        return this;
    }

    public void setVegatarian(Boolean vegatarian) {
        this.vegatarian = vegatarian;
    }

    public Integer getNrOfServings() {
        return this.nrOfServings;
    }

    public Recipe nrOfServings(Integer nrOfServings) {
        this.setNrOfServings(nrOfServings);
        return this;
    }

    public void setNrOfServings(Integer nrOfServings) {
        this.nrOfServings = nrOfServings;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public Recipe instructions(String instructions) {
        this.setInstructions(instructions);
        return this;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getName() {
        return this.name;
    }

    public Recipe name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recipe)) {
            return false;
        }
        return id != null && id.equals(((Recipe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Recipe{" +
            "id=" + getId() +
            ", vegatarian='" + getVegatarian() + "'" +
            ", nrOfServings=" + getNrOfServings() +
            ", instructions='" + getInstructions() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
