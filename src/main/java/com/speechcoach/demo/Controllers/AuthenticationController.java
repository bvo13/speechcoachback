package com.speechcoach.demo.Controllers;

import com.speechcoach.demo.DTOs.CreateUserDto;
import com.speechcoach.demo.Services.AuthenticationService;
import com.speechcoach.demo.Util.AuthenticationResponse;
import com.speechcoach.demo.Util.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.apache.coyote.Response;
import org.hibernate.dialect.H2DurationIntervalSecondJdbcType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CreateUserDto createUserDto, HttpServletResponse httpServletResponse){
        AuthenticationResponse authenticationResponse = authenticationService.register(createUserDto);
        ResponseCookie accessCookie = ResponseCookie.from("access_token", authenticationResponse.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Strict")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authenticationResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok().headers(httpHeaders ->{
            httpHeaders.add(HttpHeaders.SET_COOKIE,accessCookie.toString());
            httpHeaders.add(HttpHeaders.SET_COOKIE,refreshCookie.toString());

                }
                ).build();

    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        AuthenticationResponse authenticationResponse = authenticationService.login(loginRequest);
        ResponseCookie accessCookie = ResponseCookie.from("access_token", authenticationResponse.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Strict")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authenticationResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok().headers(httpHeaders -> {
            httpHeaders.add(HttpHeaders.SET_COOKIE,accessCookie.toString());
            httpHeaders.add(HttpHeaders.SET_COOKIE,refreshCookie.toString());
        }).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response){
        AuthenticationResponse authenticationResponse = authenticationService.refresh(request);
        ResponseCookie accessCookie = ResponseCookie.from("access_token", authenticationResponse.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Strict")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authenticationResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok().headers(httpHeaders -> {
            httpHeaders.add(HttpHeaders.SET_COOKIE,accessCookie.toString());
            httpHeaders.add(HttpHeaders.SET_COOKIE,refreshCookie.toString());
        }).build();
    }

}
