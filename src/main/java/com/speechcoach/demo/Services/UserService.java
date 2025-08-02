package com.speechcoach.demo.Services;

import com.speechcoach.demo.Entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserEntity save(UserEntity user);

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findUserById(Long id);

    List<UserEntity> findAllUsers();

    boolean userExists(Long id);

    UserEntity partialUpdate(Long id, UserEntity user);

    void delete(Long id);

    void deleteAll();

}
