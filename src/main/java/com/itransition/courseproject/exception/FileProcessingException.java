package com.itransition.courseproject.exception;

public class FileProcessingException extends ApiException{

    public FileProcessingException(String messageENG, String messageRUS) {
        super(messageENG, messageRUS);
    }
}
