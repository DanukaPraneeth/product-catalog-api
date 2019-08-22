package com.adcash.productcatalog.service;

import com.adcash.productcatalog.dao.CategoryServiceDao;
import com.adcash.productcatalog.model.Category;
import com.adcash.productcatalog.model.CategoryUpdateBean;
import com.adcash.productcatalog.util.BadRequestException;
import com.adcash.productcatalog.util.DataLayerException;
import com.adcash.productcatalog.util.DataNotFoundException;
import com.adcash.productcatalog.util.InternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryServiceDao categoryServiceDao;

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);


    public List<Category> getAllCategories(){

        try {
            return categoryServiceDao.getCategories();
        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while retrieving category details");
        }
    }



    public boolean addCategory(List<String> inputList){

        try {

            // validate for existing categories
            for(String categoryName : inputList) {
                Category existingCategory = categoryServiceDao.getCategory(categoryName);
                if(existingCategory.getCategoryType() != null){
                    String errorMsg = "Error while adding category details. Category already exist in the system for " + categoryName;
                    logger.error(errorMsg);
                    throw new BadRequestException(errorMsg);
                }
            }
            return categoryServiceDao.addCategory(inputList);

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while adding category details");
        }
    }




    public boolean updateCategory(List<CategoryUpdateBean> categoryList){

        try {

            // validate for existing categories before adding to the database
            for(CategoryUpdateBean categoryNames : categoryList) {
                Category existingCategory = categoryServiceDao.getCategory(categoryNames.getExistingCategory());
                if(existingCategory.getCategoryType() == null){
                    String errorMsg = "Error while updating category details. Category doesn't exist in the system for " + categoryNames.getExistingCategory();
                    logger.error(errorMsg);
                    throw new DataNotFoundException(errorMsg);
                }

                Category newCategory = categoryServiceDao.getCategory(categoryNames.getNewCategory());
                if(newCategory.getCategoryType() != null){
                    String errorMsg = "Error while updating category details. New category name already exist in the system for " + categoryNames.getNewCategory();
                    logger.error(errorMsg);
                    throw new BadRequestException(errorMsg);
                }
            }
            return categoryServiceDao.updateCategory(categoryList);

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while updating category details");
        }


    }



    public boolean deleteCategory(String category){

        Category existingCategory = null;
        try {
            existingCategory = categoryServiceDao.getCategory(category);

            if(existingCategory.getCategoryType() != null){

                List<Integer> categoryOnlyProducts = categoryServiceDao.getCategorySpecificProducts(existingCategory.getId());
                categoryServiceDao.deleteCategory(existingCategory.getId(), categoryOnlyProducts);

                return true;
            }else {
                String errorMsg = "Error while deleting category details. Category name doesn't exist in the system for " + category;
                logger.error(errorMsg);
                throw new DataNotFoundException(errorMsg);
            }

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while deleting category details");
        }
    }

}