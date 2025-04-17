package com.travel_payment.cnpm.exceptions.user;

public class EmailSenderException extends RuntimeException{
    public EmailSenderException(String message){
        super(message);
    }
}
