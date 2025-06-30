package com.hcltech.doctorpatient.exception;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {
    @Test
    void testHandle_WritesForbiddenResponse() throws Exception {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AccessDeniedException exception = new AccessDeniedException("Test denied");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        handler.handle(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        printWriter.flush();
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("\"error\": \"Forbidden\""));
        assertTrue(responseBody.contains("\"message\": \"Access is denied\""));
    }

}
