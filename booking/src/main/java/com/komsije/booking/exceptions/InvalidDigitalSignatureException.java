package com.komsije.booking.exceptions;

public class InvalidDigitalSignatureException extends RuntimeException{
    public InvalidDigitalSignatureException(String message){
        super(message);
    }
}
