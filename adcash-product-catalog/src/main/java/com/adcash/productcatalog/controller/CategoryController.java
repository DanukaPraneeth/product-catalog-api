package com.adcash.productcatalog.controller;

import com.adcash.productcatalog.model.Category;
import com.adcash.productcatalog.model.CategoryUpdateBean;
import com.adcash.productcatalog.service.CategoryService;
import com.adcash.productcatalog.util.CustomErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     *
     * Retrieve All Product Categories
     *
     **/
    @GetMapping(value = "/v1/search/category", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity getAllCategories() {

        List<Category> categories =  categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);

    }

    /**
     *
     * Create a new Product Category
     *
     **/
    @PostMapping(value = "/v1/add/category",  produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity addCategory(@RequestBody Map<String,List<String>> newCategory) {

        List<String> inputList = newCategory.get("categories");

        if(inputList == null || newCategory == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "Request body cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        if(categoryService.addCategory(inputList))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }


    /**
     *
     * Update an existing Product Category
     *
     **/
    @PutMapping(value = "/v1/update/category", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity updateCategory(@RequestBody List<CategoryUpdateBean> updateList) {

        if(updateList == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "Request body cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        if(categoryService.updateCategory(updateList))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);

    }


    /**
     *
     * Delete an existing Product Category
     *
     **/

    @DeleteMapping(value = "/v1/delete/category/{category}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity deleteCategory(@PathVariable("category") String category) {

        if(category == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "Category name cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        if(categoryService.deleteCategory(category))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }


}