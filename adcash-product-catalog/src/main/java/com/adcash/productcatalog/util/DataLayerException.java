package com.adcash.productcatalog.util;

public class DataLayerException extends Exception {

    public DataLayerException(String message){
        super(message);
    }

    public DataLayerException(String message, Throwable e){
        super(message, e);
    }


}
