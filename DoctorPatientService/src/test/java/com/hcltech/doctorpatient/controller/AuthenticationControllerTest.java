package com.hcltech.doctorpatient.controller;

import com.hcltech.doctorpatient.dao.service.AuthenticationService;
import com.hcltech.doctorpatient.dto.authentication.AuthenticationRequestDto;
import com.hcltech.doctorpatient.dto.authentication.AuthenticationResponseDto;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setMobile("1234567890");

        AuthenticationResponseDto responseDto = new AuthenticationResponseDto();
        responseDto.setMessage("Login successful");

        when(authenticationService.login(requestDto)).thenReturn(responseDto);

        ResponseEntity<AuthenticationResponseDto> response = authenticationController.login(requestDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful", response.getBody().getMessage());
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setMobile("1234567890");

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid.jwt.token");

        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        ResponseEntity<AuthenticationResponseDto> response = authenticationController.logout(requestDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout successful", response.getBody().getMessage());
    }

    @Test
    public void testLogoutFailureDueToMissingToken() {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setMobile("1234567890");

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        assertThrows(JwtException.class, () -> authenticationController.logout(requestDto));
    }
}