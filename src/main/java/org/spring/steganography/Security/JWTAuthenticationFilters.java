package org.spring.steganography.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JWTAuthenticationFilters extends OncePerRequestFilter {

    private final JWTServices jwtServices;
    private final UserDetailService userDetailService;

    public JWTAuthenticationFilters(JWTServices jwtServices, UserDetailService userDetailService) {
        this.jwtServices = jwtServices;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader =request.getHeader("Authorization");

    }
}
