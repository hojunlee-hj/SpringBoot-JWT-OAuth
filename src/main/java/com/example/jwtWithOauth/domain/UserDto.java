package com.example.jwtWithOauth.domain;

import lombok.Data;
import lombok.Getter;

@Data
public class UserDto {

    private String email;
    private String password;

    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
