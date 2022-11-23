package com.example.jwtWithOauth.repository.refreshtoken;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@RedisHash(value = "refreshToken", timeToLive = 60)
@Getter
public class RefreshToken {

    @Id
    private String refreshToken;
    private Long userId;

    public RefreshToken(final String refreshToken, final Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

}
