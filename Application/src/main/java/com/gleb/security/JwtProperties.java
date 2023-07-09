package com.gleb.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private final String secretKey = "flwecvevervrwrb3RvrvrVev4rvzxsqcysyhljt";

    //validity in milliseconds
    private final long validityInMs = 3600000;
}
