package com.speechcoach.demo.Repositories;

import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Util.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Transactional
    @Modifying
    void deleteAllByUser(UserEntity user);
    Optional<RefreshToken> findFirstByUserOrderByExpirationDateDesc(UserEntity user);
}
