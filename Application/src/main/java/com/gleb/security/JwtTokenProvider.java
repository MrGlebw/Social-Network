package com.gleb.security;

import com.gleb.data.RefreshToken;
import com.gleb.repo.RefreshTokenRepo;
import com.gleb.repo.UserRepo;
import com.gleb.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static java.util.stream.Collectors.joining;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "roles";
    private static final String REFRESH_TOKEN_AUTHORITIES_KEY = "refresh_token";

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

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
        } catch (Exception e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }



    public boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.info("Invalid refresh token: {}", e.getMessage());
            log.trace("Invalid refresh token trace.", e);
            return false;
        }
    }

    public String createAccessTokenFromRefreshToken(String refreshToken) {
        // Get the user details from the refresh token, assuming you have a method to get the user from the refresh token
        UserDetails userDetails = getUserDetailsFromRefreshToken(refreshToken);

        // Generate a new access token for the user
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        if (!authorities.isEmpty()) {
            claims.put(AUTHORITIES_KEY, authorities.stream()
                    .map(GrantedAuthority::getAuthority).collect(joining(",")));
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getExpirationInMs());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken);

            // Extract user details from the claims and return the UserDetails object
            String username = claims.getBody().getSubject();
            com.gleb.data.User user = userRepo.findByUsername(username).block(); // Blocking call

            if (user != null) {
                // Create and return the UserDetails object based on the User entity
                // You may need to adjust this based on your User entity structure
                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.emptyList()
                );
            } else {
                throw new UsernameNotFoundException("User not found for refresh token: " + refreshToken);
            }
        } catch (Exception e) {
            log.info("Error getting user details from refresh token: {}", e.getMessage());
            log.trace("Error getting user details from refresh token trace.", e);
            throw new RuntimeException("Invalid refresh token", e);
        }
    }

    public void saveRefreshTokenToDatabase(String refreshToken, String username) {
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUserId(username);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusMinutes((jwtProperties.getRefreshExpirationInMs()/60000)));
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());

        refreshTokenRepo.save(refreshTokenEntity).subscribe(); // Save the refresh token to the database asynchronously
    }

}
