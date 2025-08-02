package com.speechcoach.demo.Services;

import com.speechcoach.demo.Entities.SessionEntity;
import com.speechcoach.demo.Entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface SessionService {
    SessionEntity save(SessionEntity session);

    Optional<SessionEntity> findSessionById(Long id);

    List<SessionEntity> findAll();

    boolean exists(Long id);



    void delete(Long id);

    void deleteAll();

    void deleteByUser(UserEntity user);
}
