package com.hcltech.doctorpatient.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    public static final String SECRET_KEY="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public String extractUsernameFromToken(String token) {
        return extractClaimFromToken(token, Claims::getSubject);
    }

    public Date extractExpirationFromToken(String token) {
        return extractClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T extractClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaimsFromToken(String token) {
        return Jwts.parser()
                   .verifyWith(getSignKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpirationFromToken(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        final long now = System.currentTimeMillis();
        // hours + minutes + seconds + milliseconds
        final long twoHoursLater = now + 2 * 60 * 60 * 1_000;
        return Jwts.builder()
                   .subject(subject)
                   .issuedAt(new Date(now))
                   .expiration(new Date(twoHoursLater))
                   .claims(claims)
                   .signWith(getSignKey())
                   .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
