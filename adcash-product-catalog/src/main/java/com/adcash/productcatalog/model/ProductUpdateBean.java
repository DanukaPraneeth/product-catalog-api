package com.adcash.productcatalog.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductUpdateBean {

    @JsonProperty("product_code")
    int productCode;

    @JsonProperty("new_product_details")
    private Product product;

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


}
