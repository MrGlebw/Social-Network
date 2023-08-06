package com.gleb.config;


import com.gleb.data.user.Roles;
import com.gleb.security.JwtTokenAuthFilter;
import com.gleb.security.JwtTokenProvider;
import com.gleb.service.user.UserDetailsServiceImpl;
import com.gleb.util.wrapper.UserWrapperService;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    private final ReactiveUserDetailsService userDetailsService;
    private final UserWrapperService userWrapperService;

    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsServiceImpl userDetailsService, UserWrapperService userWrapperService) {
        this.userDetailsService = userDetailsService;
        this.userWrapperService = userWrapperService;
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
                        .pathMatchers(HttpMethod.DELETE, "/users/**").access((authentication, object) -> isAdmin(authentication))
                        .pathMatchers(HttpMethod.GET, "/users/id/**").access((authentication, object) -> isAdmin(authentication))
                        .pathMatchers(HttpMethod.GET, "/users/username/**").authenticated()
                        .pathMatchers(HttpMethod.GET, "/users/name/{firstName}+{lastName}").authenticated()
                        .pathMatchers("/me/updateName").authenticated()
                        .pathMatchers("/me/updatePassword").authenticated()
                        .pathMatchers("/me/updateEmail").authenticated()
                        .pathMatchers("/me").authenticated()
                        .pathMatchers("/me/makePrivate").authenticated()
                        .pathMatchers("/me/makePublic").authenticated()
                        .pathMatchers("/me/delete").authenticated()
                        .pathMatchers("/me/posts/**").authenticated()
                        .pathMatchers("/feed").authenticated()
                        .pathMatchers("/feed/moderatorFeed/delete/{id}").access((authentication, object) -> isModerator(authentication))
                        .pathMatchers("/feed/moderatorFeed").access((authentication, object) -> isModerator(authentication))
                        .pathMatchers("/feed/moderatorFeed/disapprove/**").access((authentication, object) -> isModerator(authentication))
                        .pathMatchers("/me/{id}/commentPost").authenticated()
                        .pathMatchers("/me/myComments").authenticated()
                        .pathMatchers("/me/{postId}/deleteComment/{commentIdForPost}").authenticated()
                        .pathMatchers("/posts/{postId}/comments").authenticated()
                        .pathMatchers("/posts/{postId}/comments/author/{authorName}").access((authentication, object) -> isModerator(authentication))
                        .pathMatchers("/posts/{postId}/comments/deleteComment/{commentIdForPost}").access((authentication, object) -> isModerator(authentication))

                        .anyExchange().permitAll()
                )
                .build();
    }


    private Mono<AuthorizationDecision> isAdmin(Mono<Authentication> authentication) {
        return authentication
                .map(Authentication::getName)
                .flatMap(userWrapperService::findUserByUsername)
                .map(user -> user.getRoles().contains(Roles.ADMIN.name()))
                .map(AuthorizationDecision::new);
    }

    private Mono<AuthorizationDecision> isModerator(Mono<Authentication> authentication) {
        return authentication
                .map(Authentication::getName)
                .flatMap(userWrapperService::findUserByUsername)
                .map(user -> user.getRoles().contains(Roles.MODERATOR.name()))
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

