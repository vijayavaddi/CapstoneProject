package com.hcltech.doctorpatient.exception;

public class DiseaseNotFoundException extends CustomException{
    public DiseaseNotFoundException(String message) {
        super(message);
    }

    public DiseaseNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
