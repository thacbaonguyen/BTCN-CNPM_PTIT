package com.travel_payment.cnpm.exceptions.common;

public class AlreadyException extends RuntimeException{
    public AlreadyException(String message){
        super(message);
    }
}
