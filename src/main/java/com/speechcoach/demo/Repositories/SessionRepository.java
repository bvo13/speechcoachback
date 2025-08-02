package com.speechcoach.demo.Repositories;

import com.speechcoach.demo.Entities.SessionEntity;
import com.speechcoach.demo.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    void deleteByUser(UserEntity user);
}
