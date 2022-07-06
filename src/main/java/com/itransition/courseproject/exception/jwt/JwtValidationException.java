package com.itransition.courseproject.exception.jwt;
public class JwtValidationException extends RuntimeException{
    public JwtValidationException(String message) {
        super(message);
    }
}
