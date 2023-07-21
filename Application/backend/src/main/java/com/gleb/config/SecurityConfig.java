package com.gleb.config;


import com.gleb.security.JwtTokenAuthFilter;
import com.gleb.security.JwtTokenProvider;
import com.gleb.service.UserDetailsServiceImpl;
import com.gleb.web.CurrentUserController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);


    private final ReactiveUserDetailsService userDetailsService;

    public SecurityConfig(@Qualifier("userDetailsServiceImpl")UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                JwtTokenProvider tokenProvider,
                                                ReactiveAuthenticationManager reactiveAuthenticationManager) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .addFilterAt(new JwtTokenAuthFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(it -> it
                        .pathMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/users/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers("/me/update").authenticated()
                        .pathMatchers("/me").authenticated()

                        .anyExchange().permitAll()
                )
                .build();
    }

    private Mono<AuthorizationDecision> currentUserMatchesPath(Mono<Authentication> authentication,
                                                               AuthorizationContext context) {
        return authentication
                .map(Authentication::getName) // Get the name of the currently authenticated user
                .flatMap(username -> {
                    Object userVariable = context.getVariables().get("user");
                    if (userVariable != null && userVariable.equals(username)) {
                        return Mono.just(true);
                    } else {
                        // You may want to log the error or handle it differently if needed.
                        return Mono.just(false);
                    }
                })
                .map(AuthorizationDecision::new);
    }

    @Bean
    @Primary
    public ReactiveUserDetailsService userDetailsService() {
        return userDetailsService;
    }


    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

