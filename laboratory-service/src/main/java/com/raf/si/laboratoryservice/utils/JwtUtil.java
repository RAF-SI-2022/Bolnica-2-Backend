package com.raf.si.laboratoryservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.Map;

@AllArgsConstructor
public class JwtUtil {

    private final String secretKey;

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String generateToken(Map<String, Object> claims, String subject){
        return  Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *10))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }
}
