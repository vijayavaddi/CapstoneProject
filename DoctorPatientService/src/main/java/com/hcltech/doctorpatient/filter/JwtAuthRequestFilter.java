package com.hcltech.doctorpatient.filter;

import com.hcltech.doctorpatient.service.JpaUserDetailsService;
import com.hcltech.doctorpatient.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthRequestFilter extends OncePerRequestFilter {

    private final JpaUserDetailsService jpaUserDetailsService;

    private final JwtUtil jwtUtil;

    public JwtAuthRequestFilter(JpaUserDetailsService jpaUserDetailsService, JwtUtil jwtUtil) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwt = authorizationHeader.substring(7);
                String username = jwtUtil.extractUsernameFromToken(jwt);
                if (username != null && SecurityContextHolder.getContext()
                        .getAuthentication() == null) {

                    UserDetails userDetails = this.jpaUserDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(jwt, userDetails)) {

                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext()
                                .setAuthentication(authenticationToken);
                    }
                }
            }
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid Token\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}
