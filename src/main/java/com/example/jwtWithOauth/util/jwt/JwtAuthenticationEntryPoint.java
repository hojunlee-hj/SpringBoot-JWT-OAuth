package com.example.jwtWithOauth.util.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.jwtWithOauth.domain.response.Message;
import com.example.jwtWithOauth.domain.response.ResponseEntity;
import com.example.jwtWithOauth.domain.response.StatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        PrintWriter writer = response.getWriter();

        Message message = Message.builder()
                                .message("JWT Authentication Error")
                                .status(StatusEnum.UNAUTHORIZED)
                                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper om = new ObjectMapper();

        try{
            writer.write(om.writeValueAsString(message));
        }catch(NullPointerException e){
            LOGGER.error("응답 메시지 작성 에러", e);
        }finally{
            if(writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}