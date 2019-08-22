package com.adcash.productcatalog.controller;

import com.adcash.productcatalog.model.Category;
import com.adcash.productcatalog.service.CategoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class CategoryControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() {
        //Reset mock between tests
        Mockito.reset(categoryService);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }



    /*

    @Test
    public void findAll_categories_ShouldReturnListOfCategories() throws Exception {

        Category category = new Category();
        category.setId(1);
        category.setCategoryType("kids");

        List<Category> categoryList = Arrays.asList(category);

        given(categoryService.getAllCategories()).willReturn(categoryList);


        this.mockMvc.perform(get("/v1/search/category").
                header("Authorization" , "Basic cm9vdDpwYXNzd29yZA==")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect((ResultMatcher) jsonPath("$[0].categoryType", is(category.getCategoryType())));

        verify(categoryService, times(1)).getAllCategories();
        verifyNoMoreInteractions(categoryService);


    }


     */
}