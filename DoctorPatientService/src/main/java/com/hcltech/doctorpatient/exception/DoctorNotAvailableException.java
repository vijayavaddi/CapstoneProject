package com.hcltech.doctorpatient.exception;

public class DoctorNotAvailableException extends CustomException{
    public DoctorNotAvailableException(String message) {
        super(message);
    }

    public DoctorNotAvailableException(String message, String errorCode) {
        super(message, errorCode);
    }
}
