package com.travel_payment.cnpm.configurations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

import java.util.function.Function;
@Service
public class JwtUtils {
    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(String username, Map<String, Object> claim){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        for (Map.Entry<String, Object> entry : claim.entrySet()) {
            claims.put(entry.getKey(), entry.getValue());
        }
        return createToken(claims, username);
    }
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims getClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims getClaimsFromToKenExpired(String token){
        try{
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (ExpiredJwtException ex){
            return ex.getClaims();
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver){
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getUserNameFromTokenExpired(String token){
        Claims claims = getClaimsFromToKenExpired(token);
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token){
        return getExpirationDateFromToken(token).before(new Date());
    }

    public Boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails){
        return !isTokenExpired(token) && userDetails.getUsername().equals(getUsernameFromToken(token));
    }
}