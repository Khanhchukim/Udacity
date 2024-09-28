package com.example.demo.security.jwt;

import com.example.demo.security.services.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.demo.security.SecurityContants.AUTHORIZATION_HEADER;
import static com.example.demo.security.SecurityContants.JWT_TOKEN_PREFIX;

@Component
public class JwtTokenVerificationFilter extends BasicAuthenticationFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtTokenVerificationFilter(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtTokenUtils jwtTokenUtils) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = resolveToken(request);

        if (isTokenInvalid(token)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(token, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    private boolean isTokenInvalid(String token) {
        return token == null || !token.startsWith(JWT_TOKEN_PREFIX);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token, HttpServletRequest request) {
        String username = this.jwtTokenUtils.extractUsername(token);
        if (username == null) {
            return null;
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (!this.jwtTokenUtils.validateToken(token, userDetails)) {
            return null;
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }

}
