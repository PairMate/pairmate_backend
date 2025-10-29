package pairmate.user_service.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Access Token 24시간 
    private static final long ACCESS_TOKEN_EXPIRE = 1000L * 60 * 60 * 24;
    // Refresh Token 7일
    private final long REFRESH_TOKEN_EXPIRE = 1000L * 60 * 60 * 24 * 7;

    public String createAccessToken(Long userId, String loginId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("loginId", loginId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId, String loginId) {
        return Jwts.builder()
                .setSubject("RefreshToken")
                .claim("userId", userId)
                .claim("loginId", loginId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
