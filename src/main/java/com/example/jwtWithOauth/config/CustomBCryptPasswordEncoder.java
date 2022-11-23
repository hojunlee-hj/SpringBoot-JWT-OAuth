package com.example.jwtWithOauth.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomBCryptPasswordEncoder extends BCryptPasswordEncoder {
}