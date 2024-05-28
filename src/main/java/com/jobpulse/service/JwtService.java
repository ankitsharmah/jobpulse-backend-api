package com.jobpulse.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String Secrete_key= "kqg234kk2j63j7hkjhg23k4j74kjbkh3kjb7j43vh4v6fd09v7z97vrd57sf0f6vr7b0b7v68xbxv75z98b786v";

    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }
    public boolean isTokenValid(String token , UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token,Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaims(String token , Function<Claims,T> claimResolver){
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignInKey() {
        byte [] keyBytes= Decoders.BASE64.decode(Secrete_key);
        return Keys.hmacShaKeyFor(keyBytes);

    }
}
