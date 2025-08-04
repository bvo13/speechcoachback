package com.speechcoach.demo.Config;

import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Services.JwtService;
import com.speechcoach.demo.Util.TokenExtractor;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final TokenExtractor tokenExtractor;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthenticationFilter(TokenExtractor tokenExtractor, JwtService jwtService, UserDetailsService userDetailsService) {
        this.tokenExtractor = tokenExtractor;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        final String jwt = tokenExtractor.extractTokenFromRequest(request, "access_token");
        final String username;
        if(jwt==null||jwt.isBlank()){
            filterChain.doFilter(request,response);
            return;
        }
        try{
            username = jwtService.extractUsername(jwt);
        }
        catch(JwtException jwtException){
            filterChain.doFilter(request,response);
            return;
        }
        if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UserEntity user =(UserEntity)userDetails;
            if(jwtService.isTokenValid(jwt,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request,response);

    }
}
