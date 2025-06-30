package com.hcltech.doctorpatient.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {
    @Test
    void testHandleDoctorPatientLimitExceeded() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        DoctorPatientLimitExceededException ex = new DoctorPatientLimitExceededException("Limit exceeded");
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("uri=/test");
    }

    @Test
    void testHandleEntityNotFound() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        jakarta.persistence.EntityNotFoundException ex = new jakarta.persistence.EntityNotFoundException("Entity not found");
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("uri=/entity");

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Entity Not Found", response.getBody().getError());
        assertEquals("Entity not found", response.getBody().getMessage());
        assertEquals("uri=/entity", response.getBody().getPath());
    }

    @Test
    void testHandleAccessDenied() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        AccessDeniedException ex = new AccessDeniedException("Access is denied");
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("uri=/forbidden");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Access Denied", response.getBody().getError());
        assertEquals("Access is denied", response.getBody().getMessage());
        assertEquals("uri=/forbidden", response.getBody().getPath());
    }
    @Test
    void testHandleGenericException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception ex = new Exception("Something went wrong");
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("uri=/error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Something went wrong", response.getBody().getMessage());
        assertEquals("uri=/error", response.getBody().getPath());
    }


    @Test
    void testHandleDoctorNotAvailableException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        DoctorNotAvailableException ex = new DoctorNotAvailableException("Doctor is not available");
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("uri=/doctor-unavailable");
        ResponseEntity<ErrorResponse> response = handler.DoctorNotAvailableException(ex, request);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Doctor Unavailable", response.getBody().getError());
        assertEquals("Doctor is not available", response.getBody().getMessage());
        assertEquals("uri=/doctor-unavailable", response.getBody().getPath());
    }

    @Test
    void testHandleDiseaseNotFoundException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        DiseaseNotFoundException ex = new DiseaseNotFoundException("Disease not found in records");
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("uri=/disease-not-found");

        ResponseEntity<ErrorResponse> response = handler.DiseaseNotFoundException(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Disease not found", response.getBody().getError());
        assertEquals("Disease not found in records", response.getBody().getMessage());
        assertEquals("uri=/disease-not-found", response.getBody().getPath());
    }

    @Test
    void testHandleDuplicateAppointmentException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        DuplicateAppointmentException ex = new DuplicateAppointmentException("Slot already booked");
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("uri=/duplicate-appointment");

        ResponseEntity<ErrorResponse> response = handler.DuplicateAppointmentException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ALready booked slot", response.getBody().getError());
        assertEquals("Slot already booked", response.getBody().getMessage());
        assertEquals("uri=/duplicate-appointment", response.getBody().getPath());
    }

    @Test
    void testHandleNotValidationException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = Mockito.mock(org.springframework.validation.BindingResult.class);
        org.springframework.validation.FieldError fieldError = new org.springframework.validation.FieldError("object", "field", "must not be null");
        java.util.List<org.springframework.validation.FieldError> fieldErrors = java.util.Collections.singletonList(fieldError);

        Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<Map<String, String>> response = handler.handleNotValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("must not be null", response.getBody().get("field"));
    }

}
