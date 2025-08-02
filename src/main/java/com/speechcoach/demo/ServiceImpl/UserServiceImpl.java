package com.speechcoach.demo.ServiceImpl;

import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Repositories.SessionRepository;
import com.speechcoach.demo.Repositories.UserRepository;
import com.speechcoach.demo.Services.UserService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final SessionRepository sessionRepository;
    public UserServiceImpl(UserRepository userRepository, SessionRepository sessionRepository){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }
    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<UserEntity> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<UserEntity> findAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(),false).collect(Collectors.toList());
    }

    @Override
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public UserEntity partialUpdate(Long id, UserEntity user) {
        return userRepository.findById(id).map(existingUser->{
            Optional.ofNullable(user.getUsername()).ifPresent(existingUser::setUsername);
            Optional.ofNullable(user.getName()).ifPresent(existingUser::setName);
            Optional.ofNullable(user.getPassword()).ifPresent(existingUser::setPassword);



        return userRepository.save(existingUser);
        }).orElseThrow(()-> new RuntimeException("user does not exist"));
    }

    @Override
    public void delete(Long id) {
        sessionRepository.deleteByUser
                (userRepository.findById(id).orElseThrow(()->
                        new RuntimeException("User does not exist.")));
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAll(){
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

}
