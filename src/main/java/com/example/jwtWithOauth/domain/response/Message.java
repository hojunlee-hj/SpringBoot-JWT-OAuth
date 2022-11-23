package com.example.jwtWithOauth.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    private StatusEnum status;
    private String message;
    private Object data;

    public Message() {
        this.status = StatusEnum.BAD_REQUEST;
        this.data = null;
        this.message = null;
    }
}