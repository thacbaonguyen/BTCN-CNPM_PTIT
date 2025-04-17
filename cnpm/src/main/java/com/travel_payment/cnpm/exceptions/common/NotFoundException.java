package com.travel_payment.cnpm.exceptions.common;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
