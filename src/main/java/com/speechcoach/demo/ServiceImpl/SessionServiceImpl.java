package com.speechcoach.demo.ServiceImpl;

import com.speechcoach.demo.Entities.SessionEntity;
import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Repositories.SessionRepository;
import com.speechcoach.demo.Services.SessionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public SessionEntity save(SessionEntity session) {
        return sessionRepository.save(session);
    }

    @Override
    public Optional<SessionEntity> findSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    @Override
    public List<SessionEntity> findAll() {
        return StreamSupport.stream(
                sessionRepository.findAll()
                        .spliterator(),false)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(Long id) {
        return sessionRepository.existsById(id);
    }


    @Override
    public void delete(Long id) {

        sessionRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        sessionRepository.deleteAll();
    }

    @Override
    public void deleteByUser(UserEntity user) {
        sessionRepository.deleteByUser(user);
    }
}
