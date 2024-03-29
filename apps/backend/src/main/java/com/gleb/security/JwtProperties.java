package com.gleb.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expirationInMs;
    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.refreshExpirationInMs}")
    private Long refreshExpirationInMs;

}
