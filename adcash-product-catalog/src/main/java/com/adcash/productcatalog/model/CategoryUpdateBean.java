package com.adcash.productcatalog.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryUpdateBean {

    @JsonProperty("existing_category")
    private String existingCategory;

    @JsonProperty("new_category")
    private String newCategory;

    public String getExistingCategory() {
        return existingCategory;
    }

    public void setExistingCategory(String existingCategory) {
        this.existingCategory = existingCategory;
    }

    public String getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(String newCategory) {
        this.newCategory = newCategory;
    }

}
