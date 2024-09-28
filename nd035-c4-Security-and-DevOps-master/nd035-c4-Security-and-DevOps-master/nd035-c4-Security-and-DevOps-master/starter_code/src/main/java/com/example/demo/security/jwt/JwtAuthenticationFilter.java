package com.example.demo.security.jwt;

import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.example.demo.security.SecurityContants.AUTHORIZATION_HEADER;
import static com.example.demo.security.SecurityContants.JWT_TOKEN_PREFIX;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Optional<User> credentialsOpt = Optional.ofNullable(new ObjectMapper().readValue(request.getInputStream(), User.class));
            if (credentialsOpt.isEmpty()) {
                throw new AuthenticationServiceException("Missing authentication credentials");
            }
            User credentials = credentialsOpt.get();
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error reading authentication request", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String token = jwtTokenUtils.generateJwtToken(authResult);

        if (token != null && !token.isEmpty()) {
            addTokenToResponse(response, token);
        } else {
            throw new ServletException("Failed to generate JWT token");
        }
    }

    private void addTokenToResponse(HttpServletResponse response, String token) {
        response.addHeader(AUTHORIZATION_HEADER, JWT_TOKEN_PREFIX + token);
    }
}
