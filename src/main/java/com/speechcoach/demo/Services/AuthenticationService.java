package com.speechcoach.demo.Services;

import com.speechcoach.demo.DTOs.CreateUserDto;
import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Repositories.RefreshTokenRepository;
import com.speechcoach.demo.Repositories.UserRepository;
import com.speechcoach.demo.Role;
import com.speechcoach.demo.Util.AuthenticationResponse;
import com.speechcoach.demo.Util.LoginRequest;
import com.speechcoach.demo.Util.RefreshToken;
import com.speechcoach.demo.Util.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public AuthenticationResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        var user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(()-> new UsernameNotFoundException("user not found"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String accessToken = jwtService.generateToken(claims,user,1000*60*15);
        String refreshToken = jwtService.generateToken(claims,user, 1000*60*60*24*7);
        RefreshToken token = new RefreshToken();
        token.setExpirationDate(Instant.now().plus(Duration.ofDays(7)));
        token.setUser(user);
        token.setJwt(refreshToken);
        refreshTokenRepository.save(token);
        return new AuthenticationResponse(accessToken,refreshToken);
    }
    public void logout(){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserEntity){
            refreshTokenRepository.deleteAllByUser((UserEntity) principal);
        }
        else{
            throw new RuntimeException("User not found");
        }
    }

    public AuthenticationResponse refresh(HttpServletRequest request){
        String refreshCookie = tokenExtractor.extractTokenFromRequest(request, "refresh_token");
        String email = jwtService.extractUsername(refreshCookie);
        UserEntity user = userRepository.findByUsername(email).orElseThrow(()->new UsernameNotFoundException("user not found"));

        RefreshToken databaseToken = refreshTokenRepository.findFirstByUserOrderByExpirationDateDesc(user).orElseThrow(
                ()->new RuntimeException("token not found"));
        if(databaseToken==null){
            throw new RuntimeException("token not found in database");
        }
        if(!refreshCookie.equals(databaseToken.getJwt())){
            throw new RuntimeException("tokens are not the same");
        }
        if(!jwtService.isTokenValid(refreshCookie,user)){
            throw new RuntimeException("not a valid token");
        }
        String accessToken = jwtService.generateToken(user, 1000*60*15);
        String refreshToken = jwtService.generateToken(user, 1000*60*60*24*7);
        refreshTokenRepository.deleteAllByUser(user);
        RefreshToken token = new RefreshToken();
        token.setJwt(refreshToken);
        token.setUser(user);
        token.setExpirationDate(Instant.now().plus(Duration.ofDays(7)));
        refreshTokenRepository.save(token);

        return new AuthenticationResponse(accessToken,refreshToken);

    }
}
