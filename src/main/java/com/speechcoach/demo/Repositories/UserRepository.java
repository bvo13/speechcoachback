package com.speechcoach.demo.Repositories;

import com.speechcoach.demo.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    void deleteAll();
    Optional<UserEntity> findByUsername(String username);
}
