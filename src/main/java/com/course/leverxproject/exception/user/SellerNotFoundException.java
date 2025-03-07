package com.course.leverxproject.exception.user;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(String message){
        super(message);
    }
}
