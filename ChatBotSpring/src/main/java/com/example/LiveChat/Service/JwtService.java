package com.example.LiveChat.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {

    private static final String SECRET_KEY_STRING = "4ee2b62aad19adfb42a3044a003aeac0561c9c6d284dc21a315d3326a513dee6b6bac1d27133552dbb004268afb3ffb67acf511e248a972ebb835b3ad09f2338";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 30 minutes

    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    /** 🔹 Generate a new JWT Token */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
    }

    /** 🔹 Extract Email from Token */
    public String extractEmail(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            System.out.println("Error extracting email: " + e.getMessage());
            return null;
        }
    }

    /** 🔹 Validate JWT Token */
    public boolean validateToken(String token) {
        if (token == null || invalidatedTokens.contains(token)) {
            return false;
        }
        try {
            Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token has expired: " + e.getMessage());
            return false;
        } catch (JwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false;
        }
    }

    /** 🔹 Invalidate Token (Logout Functionality) */
    public void invalidateToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            invalidatedTokens.add(token);
            System.out.println("Token invalidated: " + token);
        }
    }

}

