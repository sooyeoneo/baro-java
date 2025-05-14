package com.example.barojava.jwt;

import com.example.barojava.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiry;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access / Refresh 토큰 생성
    public String createAccessToken(String username, Set<Role> roles) {
        return createToken(username, roles, accessTokenExpiry);
    }
    public String createRefreshToken(String username) {
        return createToken(username, Collections.emptySet(), refreshTokenExpiry);
    }

    private String createToken(String username, Set<Role> roles, long ttl) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttl);
        Map<String,Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(Enum::name).toList());

        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public Set<Role> getRoles(String token) {
        List<String> names = parseClaims(token).get("roles", List.class);
        return names.stream().map(Role::valueOf).collect(Collectors.toSet());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
