package com.example.jwtWithOauth.config.jwt;

public interface JwtProperties {
    String SECRET = "Cajun7";
    int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
