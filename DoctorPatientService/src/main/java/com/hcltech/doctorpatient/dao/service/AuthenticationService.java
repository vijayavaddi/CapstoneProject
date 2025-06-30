package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.dto.authentication.AuthenticationRequestDto;
import com.hcltech.doctorpatient.dto.authentication.AuthenticationResponseDto;
import com.hcltech.doctorpatient.repository.UserRepository;
import com.hcltech.doctorpatient.service.JpaUserDetailsService;
import com.hcltech.doctorpatient.util.JwtUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthenticationService {


    @Autowired
    public JpaUserDetailsService jpaUserDetailsService;
    @Autowired
    public JwtUtil jwtUtil;;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;


    public AuthenticationService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    public AuthenticationResponseDto login(AuthenticationRequestDto authenticationRequestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDto.getMobile(),
                                                        authenticationRequestDto.getPassword()));
        if (authentication.isAuthenticated()) {
            final UserDetails userDetails = jpaUserDetailsService.loadUserByUsername(
                    authenticationRequestDto.getMobile());
            final String jwt = jwtUtil.generateToken(userDetails);
            return new AuthenticationResponseDto("Log in Successfull",jwt);
        }
        throw new UsernameNotFoundException(authenticationRequestDto.getMobile() + " not found");
    }

}
