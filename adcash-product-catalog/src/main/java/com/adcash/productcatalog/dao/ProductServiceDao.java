package com.adcash.productcatalog.dao;

import com.adcash.productcatalog.model.Product;
import com.adcash.productcatalog.model.ProductUpdateBean;
import com.adcash.productcatalog.util.DataLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class ProductServiceDao {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceDao.class);

    @Autowired
    DataSource ds;

    public List<Product> getProductList() throws DataLayerException {

        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ArrayList<Product> productsList = new ArrayList<>();

        try {
            con = ds.getConnection();

            String sql1 = "select * from product";
            String sql2 = "select a.category_type from category a inner join product_category b on b.category_id = a.id where b.product_id = ?";

            ps1 = con.prepareStatement(sql1);
            rs1 = ps1.executeQuery();

            while (rs1.next()){
                Product tempProduct = new Product();
                tempProduct.setProductName(rs1.getString("product_name"));
                tempProduct.setProductCode(rs1.getInt("product_code"));

                ps2 = con.prepareStatement(sql2);
                ps2.setInt(1, rs1.getInt("id"));

                rs2 = ps2.executeQuery();

                List<String> categoryList = new ArrayList<>();
                while (rs2.next()){
                    categoryList.add(rs2.getString("category_type"));
                }
                tempProduct.setCategory(categoryList);
                productsList.add(tempProduct);
            }

        } catch (SQLException e) {
            String errorMsg = "Error in database operation while retrieving all product details";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps1, con, rs1);
            closeAllConnections(ps2, con, rs2);
        }
        return productsList;
    }



    public Product getProductByCode(int productCode) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Product productDetails = new Product();

        try {
            con = ds.getConnection();

            String sql = "select * from product where product_code = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, productCode);

            rs = ps.executeQuery();

            if (rs.next()){
                productDetails.setId(rs.getInt("id"));
                productDetails.setProductName(rs.getString("product_name"));
                productDetails.setProductCode(rs.getInt("product_code"));
            }

        } catch (SQLException e) {
            String errorMsg = "Error in database operation while retrieving product details for " + productCode;
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return productDetails;
    }



    public List<Product> getProductByCategory(String category) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Product> productList = new ArrayList();

        try {
            con = ds.getConnection();

            String sql = "select p.product_name, p.product_code from product p " +
                    "inner join product_category pc on pc.product_id = p.id " +
                    "inner join category c on c.id = pc.category_id where c.category_type = ? ";

            ps = con.prepareStatement(sql);
            ps.setString(1, category);
            rs = ps.executeQuery();


            while (rs.next()){
                Product tempProduct = new Product();
                tempProduct.setProductName(rs.getString("product_name"));
                tempProduct.setProductCode(rs.getInt("product_code"));
                tempProduct.setCategory(new ArrayList<>( Arrays.asList(category) ));
                productList.add(tempProduct);
            }

        } catch (SQLException e) {
            String errorMsg = "Error in database operation while retrieving product details for category " + category;
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return productList;
    }



    public boolean addProducts(List<Product> productList) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        boolean status = false;

        String sql_insert_product = "insert into product (product_name, product_code) values ( ?,? )";
        String sql_search_product_id = "SET @product_id = LAST_INSERT_ID()";
        String sql_get_category_id = "select id from category where category_type = ?";
        String sql_add_product_category = "insert into product_category (product_id, category_id) values (@product_id, ?)";

        try {
            con = ds.getConnection();

            con.setAutoCommit(false);

            for(Product newProduct : productList){


                ps = con.prepareStatement(sql_insert_product);
                ps.setString(1,newProduct.getProductName());
                ps.setInt(2,newProduct.getProductCode());
                ps.executeUpdate();


                ps = con.prepareStatement(sql_search_product_id);
                ps.executeQuery();

                ps2 = con.prepareStatement(sql_add_product_category);

                for(String category : newProduct.getCategory()){

                    ps = con.prepareStatement(sql_get_category_id);
                    ps.setString(1,category);
                    rs = ps.executeQuery();

                    if(rs.next()) {
                        int categoryId = rs.getInt("id");
                        ps2.setInt(1, categoryId);
                        ps2.executeUpdate();
                    }else {
                        throw new SQLException("Invalid field for product category");
                    }
                }
            }
            con.commit();
            status = true;

        } catch (SQLException e) {
            try {
                con.rollback();
                status = false;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String errorMsg = "Error in database operation while adding new product details ";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
            closeAllConnections(ps2, con, rs);
        }
        return status;
    }



    public boolean updateProduct(List<ProductUpdateBean> updateList ) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        boolean status = false;

        // Updating the product details was one in few steps in a mysql transaction
        //          update the product details in the product table
        //          get the product id for related updates in other two tables
        //          delete existing category details for the product in the product to category mapping table
        //          insert mapping details for the new list of categories

        String sql_insert_product = "update product set product_name = ?, product_code = ? where product_code = ?";
        String sql_search_product_id = "select id from product where product_code = ?";
        String sql_delete_existing_catogories = "delete from product_category where product_id = ?";
        String sql_get_category_id = "select id from category where category_type = ?";
        String sql_add_product_category = "insert into product_category (product_id, category_id) values (?,?)";

        try {
            con = ds.getConnection();

            con.setAutoCommit(false);

            for(ProductUpdateBean newProduct : updateList){

                ps = con.prepareStatement(sql_insert_product);
                ps.setString(1,newProduct.getProduct().getProductName());
                ps.setInt(2,newProduct.getProduct().getProductCode());
                ps.setInt(3,newProduct.getProductCode());
                ps.executeUpdate();

                ps = con.prepareStatement(sql_search_product_id);
                ps.setInt(1,newProduct.getProduct().getProductCode());
                rs = ps.executeQuery();

                int productId =0;
                if(rs.next())
                    productId = rs.getInt("id");

                ps = con.prepareStatement(sql_delete_existing_catogories);
                ps.setInt(1, productId);
                ps.executeUpdate();

                ps2 = con.prepareStatement(sql_add_product_category);

                for(String category : newProduct.getProduct().getCategory()){

                    ps = con.prepareStatement(sql_get_category_id);
                    ps.setString(1,category);
                    rs = ps.executeQuery();
                    if(rs.next()) {
                        int categoryId = rs.getInt("id");

                        ps2.setInt(1, productId);
                        ps2.setInt(2, categoryId);
                        ps2.executeUpdate();
                    }else {
                        throw new SQLException("Invalid field for product category");
                    }
                }
            }
            con.commit();
            status = true;

        } catch (SQLException e) {
            try {
                con.rollback();
                status = false;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String errorMsg = "Error in database operation while updating product details ";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
            closeAllConnections(ps2, con, rs);
        }
        return status;
    }



    public boolean deleteProducts(int productId) throws DataLayerException {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        boolean status = false;

        try {
            con = ds.getConnection();

            String deleteFromMappingSql = "delete from product_category where product_id = ?" ;
            String deleteDromProductSql = "delete from product where id = ?" ;

            ps1 = con.prepareStatement(deleteFromMappingSql);
            ps2 = con.prepareStatement(deleteDromProductSql);
            con.setAutoCommit(false);

            ps1.setInt(1, productId);
            ps2.setInt(1, productId);

            ps1.executeUpdate();
            ps2.executeUpdate();

            con.commit();
            status = true;

        } catch (SQLException e) {
            try {
                con.rollback();
                status = false;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            String errorMsg = "Error in database operation while deleting product details ";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps1, con, rs);
            closeAllConnections(ps2, con, rs);
        }
        return status;
    }



    /**
     * Close all connections.
     *
     * @param preparedStatement the prepared statement
     * @param connection        the connection
     * @param resultSet         the result set
     */
    public void closeAllConnections(PreparedStatement preparedStatement, Connection connection,
                                    ResultSet resultSet) {

        closeConnection(connection);
        closeStatement(preparedStatement);
        closeResultSet(resultSet);
    }


    /**
     * Close connection.
     *
     * @param dbConnection the db connection
     */
    private void closeConnection(Connection dbConnection) {

        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                logger.warn("database error. Could not close database connection. continuing with others. - " + e.getMessage(),e);
            }
        }

    }

    /**
     * Close result set.
     *
     * @param resultSet the result set
     */
    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.warn("Database error. Could not close ResultSet  - " + e.getMessage(), e);
            }
        }

    }

    /**
     * Close statement.
     *
     * @param preparedStatement the prepared statement
     */
    private void closeStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                logger.warn("Database error. Could not close PreparedStatement. Continuing with" + " others. - "+ e.getMessage(), e);
            }
        }
    }

}