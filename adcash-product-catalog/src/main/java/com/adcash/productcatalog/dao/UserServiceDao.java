package com.adcash.productcatalog.dao;

import com.adcash.productcatalog.model.User;
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
public class UserServiceDao {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceDao.class);

    @Autowired
    DataSource ds;


    public List<User> getUserList() throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<User> userList = new ArrayList<>();

        try {
            con = ds.getConnection();

            String sql = "select * from user";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                User tempUser = new User();
                tempUser.setName(rs.getString("name"));
                tempUser.setAdminUser(rs.getBoolean("is_admin"));

                userList.add(tempUser);
            }
        } catch (Exception e) {
            String errorMsg = "Error in database operation while retrieving user details ";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return userList;
    }



    public List<User> getUser(String name) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<User> userList = new ArrayList<>();

        try {
            con = ds.getConnection();

            String sql = "select * from user where name = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();

            while (rs.next()){
                User tempUser = new User();
                tempUser.setName(rs.getString("name"));
                tempUser.setAdminUser(rs.getBoolean("is_admin"));

                userList.add(tempUser);
            }

        } catch (Exception e) {
            String errorMsg = "Error in database operation while retrieving user details for user " + name;
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return userList;
    }



    public boolean updateUser(String name, User userInfo) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;

        try {
            con = ds.getConnection();

            String sql = "update user set name = ?, password = ?, is_admin = ? where name = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, userInfo.getName());
            ps.setString(2, userInfo.getPassword());
            ps.setBoolean(3, userInfo.isAdminUser());
            ps.setString(4, name);
            ps.executeUpdate();

            result = true;

        } catch (Exception e) {
            String errorMsg = "Error in database operation while updating user details for user " + name;
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return result;
    }



    public boolean addUser(List<User> userInfo) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;

        try {
            con = ds.getConnection();

            String sql = "insert into user (name, password, is_admin) values ( ?,?,?)";

            ps = con.prepareStatement(sql);
            con.setAutoCommit(false);

            for(User tempUser : userInfo) {
                ps.setString(1, tempUser.getName());
                ps.setString(2, tempUser.getPassword());
                ps.setBoolean(3, tempUser.isAdminUser());

                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();

            result = true;

        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String errorMsg = "Error in database operation while adding new user details for user ";
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return result;
    }




    public boolean deleteUser(String userName) throws DataLayerException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;

        try {
            con = ds.getConnection();

            String sql = "delete from user where name = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, userName);
            ps.executeUpdate();

            result = true;

        } catch (Exception e) {
            result = false;
            String errorMsg = "Error in database operation while deleting user details for user " + userName;
            logger.error(errorMsg, e);
            throw new DataLayerException(errorMsg, e);

        } finally {
            closeAllConnections(ps, con, rs);
        }
        return result;
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