package com.example.jwtWithOauth.controller;

import com.example.jwtWithOauth.config.jwt.JwtProperties;
import com.example.jwtWithOauth.domain.dto.LoginRequestDto;
import com.example.jwtWithOauth.domain.User;
import com.example.jwtWithOauth.domain.UserRole;
import com.example.jwtWithOauth.domain.dto.RefreshTokenDto;
import com.example.jwtWithOauth.domain.dto.TokenDto;
import com.example.jwtWithOauth.domain.dto.TokenResponse;
import com.example.jwtWithOauth.domain.response.Message;
import com.example.jwtWithOauth.domain.response.ResponseEntity;
import com.example.jwtWithOauth.domain.response.StatusEnum;
import com.example.jwtWithOauth.repository.refreshtoken.RefreshToken;
import com.example.jwtWithOauth.repository.refreshtoken.RefreshTokenRepository;
import com.example.jwtWithOauth.service.TokenService;
import com.example.jwtWithOauth.service.UserService;
import com.example.jwtWithOauth.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

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
    public String signIn(Authentication authentication) {
//        System.out.println("user = " + userDto);
//        return userService.login(userDto);
        return "JWT";
    }


    @PostMapping("/authenticate")
    public ResponseEntity<Message> authorize(@RequestBody LoginRequestDto loginRequestDto) {

        System.out.println("loginRequestDto.getEmail() = " + loginRequestDto.getEmail());

        User findUser = userService.login(loginRequestDto);
        String accessToken = tokenProvider.createToken(findUser, JwtProperties.EXPIRATION_TIME);
        String refreshToken = tokenProvider.createToken(findUser, JwtProperties.REFRESH_EXPIRATION_TIME);

        /**
         * Todo : 1. Refresh Token 발행, Refresh Token Redis 저장, logOut 시나리오 구현
         */

        refreshTokenRepository.save(new RefreshToken(refreshToken, findUser.getId()));

        System.out.println("jwt = " + accessToken + refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtProperties.HEADER_STRING, "Bearer " + accessToken);
        TokenResponse result = TokenResponse.builder()
                .jwt(new TokenDto(accessToken, refreshToken))
                .exist(false)
                .build();
        Message message = Message.builder().data(result).status(StatusEnum.OK).message(null).build();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/oauth/login")
    public String oauthLogin(@AuthenticationPrincipal OAuth2User oAuth2User) {
        System.out.println("oAuth2User = " + oAuth2User);
        return "JWT";
    }

    @PostMapping("/accessToken")
    /**
     *  {
     *      refreshToken : xx
     *  }
     */
    public ResponseEntity<Message> accessToken(@RequestBody RefreshTokenDto refreshToken) {
        System.out.println("refreshToken = " + refreshToken);
        String accessToken = tokenService.generateAccessToken(refreshToken.getRefreshToken());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtProperties.HEADER_STRING, "Bearer " + accessToken);
        TokenResponse result = TokenResponse.builder()
                .jwt(new TokenDto(accessToken, refreshToken.getRefreshToken()))
                .exist(false)
                .build();
        Message message = Message.builder().data(result).status(StatusEnum.OK).message(null).build();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logOut(@RequestBody RefreshTokenDto refreshToken) {
        // Todo : 1. accessToken 확인 2. RefreshToken 삭제 (userId 이용) 3. BlackList (유효기간동안 접근시)
        Message message = Message.builder().data("ㅋㅋ").status(StatusEnum.OK).message(null).build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }
}
