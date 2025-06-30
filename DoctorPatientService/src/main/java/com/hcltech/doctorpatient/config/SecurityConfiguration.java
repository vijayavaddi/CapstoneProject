package com.hcltech.doctorpatient.config;

import com.hcltech.doctorpatient.exception.CustomAccessDeniedHandler;
import com.hcltech.doctorpatient.filter.JwtAuthRequestFilter;
import com.hcltech.doctorpatient.service.JpaUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] SWAGGER_WHITE_LIST        = { "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**" };
    private static final String[] H2_CONSOLE_WHITE_LIST     = { "/h2-console/**" };
    private static final String[] AUTHENTICATION_WHITE_LIST = { "/api/v1/auth/register  ",
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/api/v1/auth/**",
    };


    private final JwtAuthRequestFilter jwtAuthRequestFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;


    private final JpaUserDetailsService jpaUserDetailsService;

    public SecurityConfiguration(JwtAuthRequestFilter jwtAuthRequestFilter, CustomAccessDeniedHandler accessDeniedHandler, JpaUserDetailsService jpaUserDetailsService) {
        this.jwtAuthRequestFilter = jwtAuthRequestFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.jpaUserDetailsService = jpaUserDetailsService;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // if allowed, you will see the popup when JWT is not sent
                // .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(SWAGGER_WHITE_LIST)
                        .permitAll()
                        .requestMatchers(H2_CONSOLE_WHITE_LIST)
                        .permitAll()
                        .requestMatchers(AUTHENTICATION_WHITE_LIST)
                        .permitAll()
                        .requestMatchers("/api/users/register")
                        .permitAll()
                        .requestMatchers("/api/users")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler))
                // tell spring security not to create any session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // .authenticationProvider(authenticationProvider(userDetailsService(passwordEncoder())))
                // authentication provider
                .authenticationProvider(authenticationProvider(jpaUserDetailsService))
                // before filter
                .addFilterBefore(jwtAuthRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(JpaUserDetailsService jpaUserDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(jpaUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
