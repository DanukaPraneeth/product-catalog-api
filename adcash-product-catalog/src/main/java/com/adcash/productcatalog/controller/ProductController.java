package com.adcash.productcatalog.controller;

import com.adcash.productcatalog.model.Product;
import com.adcash.productcatalog.model.ProductUpdateBean;
import com.adcash.productcatalog.service.ProductService;
import com.adcash.productcatalog.util.CustomErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     *
     * Retrieve All Products
     *
     **/
    @GetMapping(value = "/v1/search/product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity getAllProducts() {

        List<Product> products = productService.getProductList();
        return new ResponseEntity<>(products, HttpStatus.OK);

    }


    /**
     *
     * Retrieve Product by Category
     *
     **/
    @GetMapping(value = "/v1/search/product/{category}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity getProductByCategory(@PathVariable("category") String category) {

        if(category == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "Category cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        List<Product> products = productService.getProductByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);

    }


    /**
     *
     * Create a new Product
     *
     **/
    @PostMapping(value = "/v1/add/product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity addProduct(@RequestBody Map<String, List<Product>> productList) {

        List<Product> products = productList.get("product_list");

        if( productList == null || products == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "Request body cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        if(productService.createProduct(products))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }


    /**
     *
     * Update an existing Product
     *
     **/
    @PutMapping(value = "/v1/update/product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity updateProduct(@RequestBody List<ProductUpdateBean> productList) {

        if(productList == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "Request body cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        if(productService.updateProducts(productList))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }


    /**
     *
     * Delete an existing Product
     *
     **/
    @DeleteMapping(value = "/v1/delete/product/{productCode}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity deleteProduct(@PathVariable("productCode") int productCode) {

        if(productService.deleteProduct(productCode))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }

}