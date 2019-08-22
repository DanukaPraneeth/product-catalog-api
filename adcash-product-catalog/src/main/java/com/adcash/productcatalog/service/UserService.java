package com.adcash.productcatalog.service;

import com.adcash.productcatalog.dao.UserServiceDao;
import com.adcash.productcatalog.model.User;
import com.adcash.productcatalog.util.BadRequestException;
import com.adcash.productcatalog.util.DataLayerException;
import com.adcash.productcatalog.util.InternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {


    @Autowired
    private UserServiceDao userServiceDao;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public List<User> getAllUsers(){

        try {
            return userServiceDao.getUserList();
        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while retrieving user details");
        }
    }



    public List<User> getUser(String name){

        try {
            List<User> userInfo =  userServiceDao.getUser(name);
            if(userInfo.size() == 0){
                String errorMsg = "Error while retrieving user details. User doesn't exist in the system for " + name;
                logger.error(errorMsg);
                throw new BadRequestException(errorMsg);
            }
            return userServiceDao.getUser(name);

        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while retrieving user details");
        }
    }



    public boolean addUser(List<User> userList){

        ArrayList<User> passwordEncryptedList = new ArrayList<>();
        try{
            for(User tempUser : userList){

                List<User> existingUser = userServiceDao.getUser(tempUser.getName());

                if (existingUser.size() != 0){
                    String errorMsg = "Error while adding user details. User already exist in the system for " + tempUser.getName();
                    logger.error(errorMsg);
                    throw new BadRequestException(errorMsg);
                }
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
                passwordEncryptedList.add(tempUser);
            }

            return userServiceDao.addUser(passwordEncryptedList);
        }
        catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while adding user details");
        }
    }




    public boolean updateUser(String name, User userInfo){

        try{
            List<User> existingUser = userServiceDao.getUser(name);

            if (existingUser.size() == 0){
                String errorMsg = "Error while adding user details. User doesn't exist in the system for " + name;
                logger.error(errorMsg);
                throw new BadRequestException(errorMsg);
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));

            return userServiceDao.updateUser(name, userInfo);

        }catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while updating user details");
        }
    }



    public boolean deleteUser(String name){
        try {
            List<User> existingUser = userServiceDao.getUser(name);

            if (existingUser.size() == 0){
                String errorMsg = "Error while deleting user details. User doesn't exist in the system for " + name;
                logger.error(errorMsg);
                throw new BadRequestException(errorMsg);
            }
            return userServiceDao.deleteUser(name);
        } catch (DataLayerException e) {
            throw new InternalServiceException("Internal service error while updating user details");
        }
    }
}