package org.homeproject.ContactAPI.error;

public class InvalidPhoneNumberException extends RuntimeException{

    public InvalidPhoneNumberException(String message) {
        super(message);
    }
}
