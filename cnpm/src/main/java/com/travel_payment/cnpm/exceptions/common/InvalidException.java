package com.travel_payment.cnpm.exceptions.common;

public class InvalidException extends RuntimeException{
    public InvalidException(String message){
        super(message);
    }
}
