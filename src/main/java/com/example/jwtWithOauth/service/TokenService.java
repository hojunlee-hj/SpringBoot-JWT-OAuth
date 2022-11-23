package com.example.jwtWithOauth.service;

import com.example.jwtWithOauth.config.jwt.JwtProperties;
import com.example.jwtWithOauth.domain.User;
import com.example.jwtWithOauth.repository.UserRepository;
import com.example.jwtWithOauth.repository.refreshtoken.RefreshToken;
import com.example.jwtWithOauth.repository.refreshtoken.RefreshTokenRepository;
import com.example.jwtWithOauth.util.jwt.TokenProvider;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final SecretKey secretKey;

    public TokenService(final UserRepository memberRepository,
                        final RefreshTokenRepository refreshTokenRepository,
                        final TokenProvider tokenProvider) {
        this.userRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.secretKey = Keys.hmacShaKeyFor(JwtProperties.SECRET.getBytes(StandardCharsets.UTF_8));
        this.tokenProvider = tokenProvider;
    }

    public String generateAccessToken(final String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findById(requestRefreshToken)
                .orElseThrow(IllegalArgumentException::new);
        // Todo : RefreshToken Exception
        Long memberId = refreshToken.getUserId();
        User findUser = userRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);

        System.out.println("findUser = " + findUser);

        String accessToken = tokenProvider.createToken(findUser, JwtProperties.EXPIRATION_TIME);

        return accessToken;
    }

    public Long getUserId(final String accessToken) {
        try {
            String memberId = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
            return Long.parseLong(memberId);
        } catch (final JwtException e) {
            // Todo : InvalidAccessTokenException 만들기
            throw new IllegalArgumentException();
        }
    }

}
