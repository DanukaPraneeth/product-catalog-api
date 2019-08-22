package com.adcash.productcatalog.controller;

import com.adcash.productcatalog.model.User;
import com.adcash.productcatalog.service.UserService;
import com.adcash.productcatalog.util.CustomErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UserController {

    /**
     *
     * Retrieve All Users
     *
     **/
    @Autowired
    private UserService userService;


    @GetMapping(path = "/v1/search/user", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity getAllUsers() {

        List<User> users =  userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);

    }

    @GetMapping(path = "/v1/search/user/{name}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public  ResponseEntity getUser(@PathVariable("name") String userName) {

        if(userName == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "Username cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        List<User> users =  userService.getUser(userName);
        return new ResponseEntity<>(users, HttpStatus.OK);

    }


    /**
     *
     * Create a new User
     *
     **/
    @PostMapping(path = "/v1/add/user", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity addUser(@RequestBody List<User> userList) {


        if(userList == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "User list cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }
        if(userService.addUser(userList))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }


    /**
     *
     * Update an existing User
     *
     **/
    @PutMapping(path = "/v1/update/user/{name}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity updateUser(@PathVariable("name") String userName, @RequestBody User userInfo) {

        if(userName == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "User name cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        if(userService.updateUser(userName, userInfo))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }


    /**
     *
     * Delete an existing User
     *
     **/

    @DeleteMapping(path = "/v1/delete/user/{name}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity deleteUser(@PathVariable("name") String userName) {

        if(userName == null){
            CustomErrorResponse error = new CustomErrorResponse(HttpStatus.BAD_REQUEST,"Bad Request", "User name cannot be null");
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }

        if(userService.deleteUser(userName))
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }


}