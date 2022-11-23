package com.example.jwtWithOauth.repository;

import com.example.jwtWithOauth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);

    public Optional<User> findByEmail(String email);

    public Optional<User> findUserByEmail(String email);
}
