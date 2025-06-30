package com.hcltech.doctorpatient.controller;

import com.hcltech.doctorpatient.dao.service.AuthenticationService;
import com.hcltech.doctorpatient.dto.authentication.AuthenticationRequestDto;
import com.hcltech.doctorpatient.dto.authentication.AuthenticationResponseDto;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(
            @RequestBody AuthenticationRequestDto authenticationRequestDto) throws Exception {
        logger.info("Login attempt for username: {}", authenticationRequestDto.getMobile());
        AuthenticationResponseDto response = authenticationService.login(authenticationRequestDto);
        logger.debug("Login successful for username: {}", authenticationRequestDto.getMobile());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponseDto> logout(
            @RequestBody AuthenticationRequestDto authenticationRequestDto) throws Exception {
        logger.info("Logout attempt for username: {}", authenticationRequestDto.getMobile());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            SecurityContextHolder.clearContext();
            logger.debug("Security context cleared for logout");
            AuthenticationResponseDto response = new AuthenticationResponseDto();
            response.setMessage("Logout successful");
            logger.info("Logout successful for username: {}", authenticationRequestDto.getMobile());
            return ResponseEntity.ok(response);
        }

        logger.warn("Logout failed due to missing or invalid JWT token");
        throw new JwtException("JWT Token is missing or invalid");
    }
}
