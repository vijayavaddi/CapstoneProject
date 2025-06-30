package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.dto.authentication.AuthenticationRequestDto;
import com.hcltech.doctorpatient.dto.authentication.AuthenticationResponseDto;
import com.hcltech.doctorpatient.service.JpaUserDetailsService;
import com.hcltech.doctorpatient.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JpaUserDetailsService jpaUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(authenticationManager);
        authenticationService.jpaUserDetailsService = jpaUserDetailsService;
        authenticationService.jwtUtil = jwtUtil;
    }

    @Test
    void testLogin_SuccessfulAuthentication() {
        String mobile = "1234567890";
        String password = "password";
        String token = "mocked-jwt-token";

        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setMobile(mobile);
        requestDto.setPassword(password);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        UserDetails userDetails = new User(mobile, password, new ArrayList<>());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jpaUserDetailsService.loadUserByUsername(mobile)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        AuthenticationResponseDto response = authenticationService.login(requestDto);

        assertNotNull(response);
        assertEquals("Log in Successfull", response.getMessage());
        assertEquals(token, response.getJwt());
    }

    @Test
    void testLogin_AuthenticationFails() {
        String mobile = "1234567890";
        String password = "wrongpassword";

        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setMobile(mobile);
        requestDto.setPassword(password);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.login(requestDto);
        });

        assertEquals(mobile + " not found", exception.getMessage());
    }
}
