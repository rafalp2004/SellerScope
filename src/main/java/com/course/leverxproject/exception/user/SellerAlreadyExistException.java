package com.course.leverxproject.exception.user;

public class SellerAlreadyExistException extends RuntimeException{
    public SellerAlreadyExistException(String message) {
        super(message);
    }
}
