package org.zerock.prac.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

// Redis 캐시 매니저(TTL 30분 예시)
@Configuration
public class CacheConfig {
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory cf) {
        var serializer = new GenericJackson2JsonRedisSerializer();
        var cfg = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                );
        return RedisCacheManager.builder(cf).cacheDefaults(cfg).build();
    }
}


