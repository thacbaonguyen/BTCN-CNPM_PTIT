package com.travel_payment.cnpm.exceptions.user;

public class PermissionException extends RuntimeException{
    public PermissionException(String message){
        super(message);
    }
}