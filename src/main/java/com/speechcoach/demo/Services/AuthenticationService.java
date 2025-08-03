package com.speechcoach.demo.Services;

import com.speechcoach.demo.DTOs.CreateUserDto;
import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Repositories.RefreshTokenRepository;
import com.speechcoach.demo.Repositories.UserRepository;
import com.speechcoach.demo.Role;
import com.speechcoach.demo.Util.AuthenticationResponse;
import com.speechcoach.demo.Util.RefreshToken;
import com.speechcoach.demo.Util.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenExtractor tokenExtractor;

    public Long getCurrentUserId(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null||!authentication.isAuthenticated()){
            return null;
        }
        Object principal = authentication.getPrincipal();
        if(principal instanceof UserEntity){
            return ((UserEntity) principal).getId();
        }
        return null;
    }
    public AuthenticationResponse register(CreateUserDto createUserDto){
        UserEntity user = new UserEntity(createUserDto.getName(), createUserDto.getUsername(), createUserDto.getPassword());
        user.setRole(Role.USER);
        userRepository.save(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",user.getId());
        String accessToken = jwtService.generateToken(claims, user, 1000*60*15);
        String refreshToken = jwtService.generateToken(claims, user, 1000*60*60*24*7);
        RefreshToken token = new RefreshToken();
        token.setJwt(refreshToken);
        token.setUser(user);
        token.setExpirationDate(Instant.now().plus(Duration.ofDays(7)));
        refreshTokenRepository.save(token);
        return new AuthenticationResponse(accessToken, refreshToken);
    }
}
