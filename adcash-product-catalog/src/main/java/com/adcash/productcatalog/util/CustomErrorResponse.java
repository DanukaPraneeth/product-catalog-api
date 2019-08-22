package com.adcash.productcatalog.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomErrorResponse extends RuntimeException{

    private HttpStatus status;
    private String error;
    private String message;


    private CustomErrorResponse() {
    }


    public CustomErrorResponse(String message) {
        this.message = message;
    }

    public CustomErrorResponse(HttpStatus status, Throwable ex) {
        this.status = status;
        this.error = "Internal Server Error";
        this.message = "Unexpected error during the process";
    }

    public CustomErrorResponse(HttpStatus status, String error, String message ) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

}
