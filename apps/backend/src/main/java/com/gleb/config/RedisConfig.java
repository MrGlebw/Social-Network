//package com.gleb.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
//import org.springframework.cache.CacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.io.Serializable;
//import java.time.Duration;
//
//public class RedisConfig {
//
//    private CacheManager cacheManager;
//
//    @Value("${spring.data.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.data.redis.port}")
//    private int redisPort;
//
//    @Bean
//    public RedisTemplate<String, Serializable> redisCacheTemplate(LettuceConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setConnectionFactory(redisConnectionFactory);
//        return template;
//    }
//
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory factory) {
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//        RedisCacheConfiguration redisCacheConfiguration = config
//                .serializeKeysWith(
//                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair
//                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
//        return RedisCacheManager.builder(factory).cacheDefaults(redisCacheConfiguration)
//                .build();
//    }
//}
