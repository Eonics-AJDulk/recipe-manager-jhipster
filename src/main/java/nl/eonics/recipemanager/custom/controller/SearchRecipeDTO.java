package nl.eonics.recipemanager.custom.controller;

import java.util.List;

public class SearchRecipeDTO {

    private Boolean vegatarian;
    private Integer nrOfServings;
    private List<String> ingredientsToInclude;
    private List<String> ingredientsToExclude;

    private String instructionSearch;

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

    public List<String> getIngredientsToInclude() {
        return ingredientsToInclude;
    }

    public void setIngredientsToInclude(List<String> ingredientsToInclude) {
        this.ingredientsToInclude = ingredientsToInclude;
    }

    public List<String> getIngredientsToExclude() {
        return ingredientsToExclude;
    }

    public void setIngredientsToExclude(List<String> ingredientsToExclude) {
        this.ingredientsToExclude = ingredientsToExclude;
    }

    public String getInstructionSearch() {
        return instructionSearch;
    }

    public void setInstructionSearch(String instructionSearch) {
        this.instructionSearch = instructionSearch;
    }
}
