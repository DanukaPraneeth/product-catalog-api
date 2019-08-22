package com.adcash.productcatalog.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String categoryType;

    public Category(){}

    @JsonIgnore
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

}
