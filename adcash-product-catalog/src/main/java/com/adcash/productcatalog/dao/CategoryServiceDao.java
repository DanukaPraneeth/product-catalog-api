package com.adcash.productcatalog.dao;

import com.adcash.productcatalog.model.Category;
import com.adcash.productcatalog.model.CategoryUpdateBean;
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
import java.util.List;

@Repository
public class CategoryServiceDao {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceDao.class);

    @Autowired
    DataSource ds;


    public List<Category> getCategories() throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Category> categoryList = new ArrayList();

        try {
            con = ds.getConnection();

            String sql = "SELECT * FROM category";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();


            while (rs.next()){
                Category tempCategory = new Category();
                tempCategory.setId(rs.getInt("id"));
                tempCategory.setCategoryType(rs.getString("category_type"));
                categoryList.add(tempCategory);
            }

        } catch (SQLException e) {
            String errorMsg = "Error in database operation while retrieving category details";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return categoryList;
    }



    public Category getCategory(String category) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Category resultCategory = new Category();
        try {
            con = ds.getConnection();

            String sql = "SELECT * FROM category where category_type = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, category);

            rs = ps.executeQuery();


            if (rs.next()){
                resultCategory.setId(rs.getInt("id"));
                resultCategory.setCategoryType(rs.getString("category_type"));
            }

        } catch (SQLException e) {
            String errorMsg = "Error in database operation while retrieving category details for " + category;
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return resultCategory;
    }



    public boolean addCategory(List<String> categoryList) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        boolean status = false;

        try {
            con = ds.getConnection();

            String sql = "insert into category (category_type) values ( ? )";
            ps = con.prepareStatement(sql);
            con.setAutoCommit(false);

            for(String newCategory: categoryList){
                ps.setString(1, newCategory);
                ps.addBatch();
            }

            ps.executeBatch();
            con.commit();
            status = true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String errorMsg = "Error in database operation while adding new category details";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, null);
        }
        return status;
    }




    public boolean updateCategory(List<CategoryUpdateBean> categoryList ) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        boolean status = false;

        try {
            con = ds.getConnection();

            String sql = "update category set category_type = ? where category_type = ?;";
            ps = con.prepareStatement(sql);
            con.setAutoCommit(false);

            for(CategoryUpdateBean anotherCategory: categoryList){
                ps.setString(1, anotherCategory.getNewCategory());
                ps.setString(2, anotherCategory.getExistingCategory());
                ps.addBatch();
            }

            ps.executeBatch();
            con.commit();
            status = true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String errorMsg = "Error in database operation while updating category details";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, null);
        }
        return status;
    }




    public List<Integer> getCategorySpecificProducts(int categoryId) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Integer> productList = new ArrayList();

        try {
            con = ds.getConnection();

            String sql = "select product_id from product_category where category_id = ? " +
                    "and product_id in (select product_id from product_category " +
                    "group by product_id HAVING COUNT(product_id) = 1)";

            ps = con.prepareStatement(sql);
            ps.setInt(1, categoryId);

            rs = ps.executeQuery();

            if (rs.next())
                productList.add(rs.getInt("product_id"));

        } catch (SQLException e) {
            String errorMsg = "Error in database operation while retrieving product details for category " + categoryId;
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return productList;
    }




    public boolean deleteCategory(int categoryId, List<Integer> productList) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs = null;
        boolean status = false;

        try {
            con = ds.getConnection();


            String sql1 = "delete from product_category where category_id = ?";
            String sql2 = "delete from category where id = ?";
            String sql3 = "delete from product where id = ?" ;

            ps1 = con.prepareStatement(sql1);
            ps2 = con.prepareStatement(sql2);
            ps3 = con.prepareStatement(sql3);
            con.setAutoCommit(false);

            ps1.setInt(1, categoryId);
            ps2.setInt(1, categoryId);

            ps1.executeUpdate();
            ps2.executeUpdate();

            if(productList.size() > 0){

                for(Integer id: productList){
                    ps3.setInt(1, id);
                    ps3.addBatch();
                }
                ps3.executeUpdate();
            }
            con.commit();
            status = true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String errorMsg = "Error in database operation while deleting category details for " + categoryId;
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps1, con, rs);
            closeAllConnections(ps2, con, rs);
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