package com.example.demo.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.example.demo.security.SecurityContants.*;

@Component
public class JwtTokenUtils {

    public String generateJwtToken(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(JWT_SECRET_KEY));

    }

    public String extractUsername(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT != null ? decodedJWT.getSubject() : null;
    }

    public boolean isTokenExpired(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT != null && decodedJWT.getExpiresAt().before(new Date());
    }

    private DecodedJWT getDecodedJWT(String token) {
        String tokenWithoutPrefix = token.replace(JWT_TOKEN_PREFIX, "");
        return JWT.require(Algorithm.HMAC512(JWT_SECRET_KEY))
                .build()
                .verify(tokenWithoutPrefix);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = this.extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
