package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.model.Role;
import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private JpaUserDetailsService jpaUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jpaUserDetailsService = new JpaUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        User user = new User();
        user.setMobile("9999999999");
        user.setPassword("password");
        user.setRole(Role.PATIENT);

        when(userRepository.findByMobile("9999999999")).thenReturn(Optional.of(user));

        UserDetails userDetails = jpaUserDetailsService.loadUserByUsername("9999999999");

        assertNotNull(userDetails);
        assertEquals("9999999999", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByMobile("8888888888")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                jpaUserDetailsService.loadUserByUsername("8888888888")
        );
    }
}