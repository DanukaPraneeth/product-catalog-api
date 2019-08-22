package com.adcash.productcatalog.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private int id;

    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_code")
    private int productCode;

    @JsonProperty("product_category")
    private List<String> category;


    public Product(){

    }

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }
}
