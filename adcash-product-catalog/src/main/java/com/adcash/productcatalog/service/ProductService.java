package com.adcash.productcatalog.service;


import com.adcash.productcatalog.dao.CategoryServiceDao;
import com.adcash.productcatalog.dao.ProductServiceDao;
import com.adcash.productcatalog.model.Category;
import com.adcash.productcatalog.model.Product;
import com.adcash.productcatalog.model.ProductUpdateBean;
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
public class ProductService {

    @Autowired
    private ProductServiceDao productServiceDao;

    @Autowired
    private CategoryServiceDao categoryServiceDao;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    public List<Product> getProductList(){

        try {
            return productServiceDao.getProductList();

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while retrieving product details");
        }

    }

    public List<Product> getProductByCategory(String category){

        try {

            // validate for existing category
            Category existingCategory = categoryServiceDao.getCategory(category);
            if(existingCategory.getCategoryType() == null){
                String errorMsg = "Error while getting product details. Category doesn't exist in the system for " + category;
                logger.error(errorMsg);
                throw new BadRequestException(errorMsg);
            }
            return productServiceDao.getProductByCategory(category);

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while getting product details");
        }
    }


    public boolean createProduct(List<Product> productList){

        try {

            // validate for existing product codes
            for(Product product : productList) {
                Product existingProduct = productServiceDao.getProductByCode(product.getProductCode());
                if(existingProduct.getProductName() != null){
                    String errorMsg = "Error while adding product details. Product already exist in the system for " + product.getProductCode();
                    logger.error(errorMsg);
                    throw new BadRequestException(errorMsg);
                }

                // validate the category list of each new product
                for(String productCategories : product.getCategory()){
                    Category existingCategory = categoryServiceDao.getCategory(productCategories);
                    if(existingCategory.getCategoryType() == null){
                        String errorMsg = "Error while adding product details. Category doesn't exist in the system for " + productCategories;
                        logger.error(errorMsg);
                        throw new BadRequestException(errorMsg);
                    }
                }
            }
            return productServiceDao.addProducts(productList);

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while adding product details");
        }

    }


    public boolean updateProducts(List<ProductUpdateBean> updateList){

        try {

            // validate for existing categories before adding to the database
            for(ProductUpdateBean productList : updateList) {
                Product existingProduct = productServiceDao.getProductByCode(productList.getProductCode());
                if(existingProduct.getProductName() == null){
                    String errorMsg = "Error while updating product details. Product doesn't exist in the system for " + productList.getProductCode();
                    logger.error(errorMsg);
                    throw new DataNotFoundException(errorMsg);
                }

                // validate new product details before adding to the database
                Product newProduct = productServiceDao.getProductByCode(productList.getProduct().getProductCode());
                if( (newProduct.getProductName() != null) && (newProduct.getProductCode() != existingProduct.getProductCode()) ){
                    String errorMsg = "Error while updating product details. New product code already exist in the system for " + newProduct.getProductCode();
                    logger.error(errorMsg);
                    throw new BadRequestException(errorMsg);
                }

                // validate the category list of each new product
                for(String productCategories : productList.getProduct().getCategory()){
                    Category existingCategory = categoryServiceDao.getCategory(productCategories);
                    if(existingCategory.getCategoryType() == null){
                        String errorMsg = "Error while updating product details. Category doesn't exist in the system for " + productCategories;
                        logger.error(errorMsg);
                        throw new BadRequestException(errorMsg);
                    }
                }
            }
            return productServiceDao.updateProduct(updateList);

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while updating product details");
        }

    }


    public boolean deleteProduct(int productCode){
        // if not available, send not available
        Product productDetails = null;
        try {
            productDetails = productServiceDao.getProductByCode(productCode);

            if(productDetails.getProductName() == null){
                String errorMsg = "Error while deleting product details. Product code doesn't exist in the system for " + productCode;
                logger.error(errorMsg);
                throw new BadRequestException(errorMsg);
            }
            return productServiceDao.deleteProducts(productDetails.getId());

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while deleting product details");
        }
    }
}