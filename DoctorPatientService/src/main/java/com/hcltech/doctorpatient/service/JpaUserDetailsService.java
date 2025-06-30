package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userRepository.findByMobile(username)
                                        .orElseThrow(() -> new UsernameNotFoundException(username));

        return toUserDetails(user);
    }

    private UserDetails toUserDetails(User user) {

        return org.springframework.security.core.userdetails.User.withUsername(
                                                   user.getMobile())
                                                        .password(user.getPassword())
                                                        .roles(user.getRole().toString())
                                                        .build();

    }
}
