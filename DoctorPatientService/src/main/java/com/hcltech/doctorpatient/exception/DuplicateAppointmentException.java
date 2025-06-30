package com.hcltech.doctorpatient.exception;

public class DuplicateAppointmentException extends CustomException{
    public DuplicateAppointmentException(String message) {
        super(message);
    }

    public DuplicateAppointmentException(String message, String errorCode) {
        super(message, errorCode);
    }
}
