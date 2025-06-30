package com.hcltech.doctorpatient.exception;

public class CustomException extends RuntimeException {


    public CustomException(String message) {
        super(message);
    }
    private String errorCode;

    public CustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}