package com.example.bankcards.service;

import com.example.bankcards.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(String username, String rawPassword, String role);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    void deleteUser(Long id);
}
