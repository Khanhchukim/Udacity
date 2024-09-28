package com.example.demo.security;

public final class SecurityContants {
    public static final Long JWT_TOKEN_EXPIRATION_TIME = 1800000L; //30 minutes
    public static final String JWT_SECRET_KEY = "secret";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
}