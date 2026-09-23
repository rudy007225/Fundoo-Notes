package com.fundoo.notes.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz";
    private final long expiration = 86400000L;
    private final long resetExpiration = 900000L;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret, expiration, resetExpiration);
    }

    @Test
    void testGenerateAndExtractResetToken() {
        Long userId = 42L;
        String email = "test@example.com";

        String token = jwtUtil.generateResetToken(userId, email);
        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));

        String extractedEmail = jwtUtil.extractEmail(token);
        assertEquals(email, extractedEmail);

        Long extractedUserId = jwtUtil.extractUserIdFromToken(token);
        assertEquals(userId, extractedUserId);

        io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
        assertNotNull(claims);
        assertEquals(email, claims.getSubject());
        assertEquals(userId, claims.get("userId", Long.class));
    }
}
