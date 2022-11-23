package com.example.jwtWithOauth.controller;

import com.example.jwtWithOauth.domain.User;
import com.example.jwtWithOauth.domain.UserDto;
import com.example.jwtWithOauth.domain.UserRole;
import com.example.jwtWithOauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    @PostMapping("/test")
    public String zz(@RequestBody String name) {
        System.out.println("name = " + name);
        return "zz";
    }

    @PostMapping("/signUp")
    public String signUp(@RequestBody User user) {
        user.setRole(UserRole.USER_ROLE);
        System.out.println("user = " + user);
        userService.join(user);
        return user.getUsername();
    }

    @PostMapping("/signIn")
    public String signIn(@RequestBody UserDto userDto) {
        System.out.println("user = " + userDto);
        return userService.login(userDto);
    }

}
