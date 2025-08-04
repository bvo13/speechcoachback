package com.speechcoach.demo.Controllers;

import com.speechcoach.demo.DTOs.UserResponseDto;
import com.speechcoach.demo.DTOs.CreateUserDto;
import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Mappers.UserMapper;
import com.speechcoach.demo.Services.AuthenticationService;
import com.speechcoach.demo.Services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
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
    @PostMapping("/users/me/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(0))
                .sameSite("Strict")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(0))
                .sameSite("Strict")
                .build();
        authenticationService.logout();
        return ResponseEntity.ok().headers(httpHeaders -> {
            httpHeaders.add(HttpHeaders.SET_COOKIE,accessCookie.toString());
            httpHeaders.add(HttpHeaders.SET_COOKIE,refreshCookie.toString());
        }).build();

    }
    @PatchMapping("users/me")
    public ResponseEntity<UserResponseDto> editUser(@RequestBody CreateUserDto createUserDto){
        Long userId = authenticationService.getCurrentUserId();
        if(!userService.userExists(userId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        UserEntity user = userMapper.mapFrom(createUserDto);
        UserResponseDto userResponseDto= userMapper.mapTo(userService.partialUpdate(userId, user));
        return new ResponseEntity<>(userResponseDto,HttpStatus.OK);

    }
    @DeleteMapping("users/me")
    public ResponseEntity<?> delete(){
        Long id = authenticationService.getCurrentUserId();
        if(!(userService.userExists(id))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
