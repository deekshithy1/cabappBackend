//package com.cabapp.service;
//
//import com.cabapp.dto.UserDetailsDTO;
//import com.cabapp.utils.CustomUser;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class JwtService {
//
//    private final String SECRET_KEY = "THIS_IS_A_VERY_SECURE_SECRET_KEY_AT_LEAST_32_CHARS";
//
//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public String generateToken(CustomUser user) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("email", user.getEmail());
//        claims.put("id", user.getId());
//        claims.put("role",user.getAuthorities().iterator().next().getAuthority());
//        return createToken(claims, user.getUsername());
//    }
//
//    private String createToken(Map<String, Object> claims, String subject) {
//        long now = System.currentTimeMillis();
//        long expiration = 1000 * 60 * 60; // 1 hour
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(now))
//                .setExpiration(new Date(now + expiration))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public Boolean validateToken(String token) {
//        try {
//            Claims claims = extractClaims(token);
//            Date expirationDate = claims.getExpiration();
//            return !expirationDate.before(new Date()); // not expired
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private Claims extractClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getBody();
//    }
//
//    public UserDetailsDTO extractUserDetails(String token) {
//        Claims claims = extractClaims(token);
//        return new UserDetailsDTO(
//
//                claims.getSubject(),              // username (subject)
//                claims.get("email", String.class),
//                claims.get("id", String.class),
//                claims.get("role",String.class)
//        );
//    }
//}

package com.cabapp.service;

import com.cabapp.dto.UserDetailsDTO;
import com.cabapp.utils.CustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String SECRET_KEY = "THIS_IS_A_VERY_SECURE_SECRET_KEY_AT_LEAST_32_CHARS";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(CustomUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("id", user.getId());
        claims.put("role", user.getAuthorities().iterator().next().getAuthority());
        return createToken(claims, user.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        long expiration = 1000 * 60 * 60; // 1 hour

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expirationDate = claims.getExpiration();
            return claims.getSubject() != null && !expirationDate.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) // fixed parsing
                .getBody();
    }

    public UserDetailsDTO extractUserDetails(String token) {
        Claims claims = extractClaims(token);
        return new UserDetailsDTO(
                claims.getSubject(),              // username (subject)
                claims.get("email", String.class),
                claims.get("id", String.class),
                claims.get("role", String.class)
        );
    }
}
