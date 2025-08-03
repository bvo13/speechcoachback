package com.speechcoach.demo.Controllers;

import com.speechcoach.demo.DTOs.UserResponseDto;
import com.speechcoach.demo.DTOs.CreateUserDto;
import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Mappers.UserMapper;
import com.speechcoach.demo.Services.AuthenticationService;
import com.speechcoach.demo.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;
    public UserController(UserService userService, UserMapper userMapper, AuthenticationService authenticationService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationService = authenticationService;
    }
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateUserDto createUserDto){
        UserEntity user = userMapper.mapFrom(createUserDto);
        UserEntity savedUser = userService.save(user);
        return new ResponseEntity<>(userMapper.mapTo(savedUser), HttpStatus.CREATED);

    }

    @GetMapping("/users/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(){
        Long userId = authenticationService.getCurrentUserId();
        Optional<UserEntity> userEntity = userService.findUserById(userId);
        return userEntity.map(user -> {
            UserResponseDto userResponseDto = userMapper.mapTo(user);
            return new ResponseEntity<>(userResponseDto,HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

    }



}
