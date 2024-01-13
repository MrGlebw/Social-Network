package com.gleb.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import static java.util.stream.Collectors.joining;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "roles";

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;


    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret()); // Assuming 'secret' property contains base64-encoded secret key
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Claims claims = Jwts.claims().setSubject(username);
        if (!authorities.isEmpty()) {
            claims.put(AUTHORITIES_KEY, authorities.stream()
                    .map(GrantedAuthority::getAuthority).collect(joining(",")));
        }

        Date now = new Date();
        Date accessTokenValidity = new Date(now.getTime() + jwtProperties.getExpirationInMs());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessTokenValidity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date refreshTokenValidity = new Date(now.getTime() + jwtProperties.getRefreshExpirationInMs());

        return Jwts.builder()
                .setSubject("refresh_token")
                .setIssuedAt(now)
                .setExpiration(refreshTokenValidity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        UserDetails principal = new org.springframework.security.core.userdetails.User(
                claims.getSubject(),
                "",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug("JWT token has expired: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            // Log other exceptions, but don't include the message for JWT signature mismatch.
            if (!(e instanceof SignatureException)) {
                log.info("Invalid JWT token: {}", e.getMessage());
            }
            return false;
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken);

            return "refresh_token".equals(claims.getBody().getSubject()) &&
                    !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.info("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }


}
