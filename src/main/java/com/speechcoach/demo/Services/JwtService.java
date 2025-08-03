package com.speechcoach.demo.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, int expiry){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiry))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        return (extractUsername(token).equals(userDetails.getUsername())&&!isTokenExpired(token));
    }

    private Key getSignInKey(){
        byte[] keyDecode = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyDecode);
    }
    public String generateToken(UserDetails userDetails, int expiry){
        Map<String, Object> extraClaims = new HashMap<>();
        return generateToken(extraClaims,userDetails,expiry);
    }



}
