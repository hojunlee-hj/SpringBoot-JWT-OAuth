package com.example.jwtWithOauth.service;

import com.example.jwtWithOauth.config.CustomBCryptPasswordEncoder;
import com.example.jwtWithOauth.domain.User;
import com.example.jwtWithOauth.domain.UserDto;
import com.example.jwtWithOauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomBCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Long join(User user) {
        validateDuplicateUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("user.getPassword() = " + user.getPassword());
        userRepository.save(user);
        return user.getId();
    }

    // validateDuplicationUser By Email
    private void validateDuplicateUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent((e)-> {throw new IllegalStateException("이미 존재하는 회원입니다.");});
    }

    public String login(UserDto userDto){
        User user = userRepository.findUserByEmail(userDto.getEmail())
                .orElseThrow(()-> new NoSuchElementException("해당 회원이 없습니다."));
        if(!passwordEncoder.matches(userDto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 잘못되었습니다.");
        }else{
            System.out.println("JWT Token 만들어야함");
            return "JWT";
        }
    }
}
