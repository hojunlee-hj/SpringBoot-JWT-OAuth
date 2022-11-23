package com.example.jwtWithOauth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class TokenDto {
    private String AccessToken;
    private String RefreshToken;
}
