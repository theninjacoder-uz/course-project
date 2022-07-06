package com.itransition.courseproject.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {

    private String messageENG;

    private String messageRUS;

    private ApiException(String message) {
        super(message);
    }

    public ApiException(String messageENG, String messageRUS) {
        this.messageENG = messageENG;
        this.messageRUS = messageRUS;
    }
}
