package com.hcltech.doctorpatient.exception;

public class DoctorPatientLimitExceededException  extends CustomException {
    public DoctorPatientLimitExceededException(String message) {
        super(message);
    }
}
