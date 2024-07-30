package ru.sparural.engine.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import ru.sparural.engine.loymax.cache.LoymaxCache;
import ru.sparural.engine.utils.CacheConstants;

import java.time.Duration;

import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.ACCOUNT_CACHE;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.CARDS_CACHE;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.CHECK_CACHE;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.FAVORITE_CATEGORIES_CACHE;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.MOBILE_MAIN;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.OFFERS_CACHE;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.PERSONAL_GOODS_CACHE;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.PERSONAL_OFFERS_CACHE;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.STATUS_CACHE;
import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.USER_CACHE;

/**
 * @author Vorobyev Vyacheslav
 */
@Configuration
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@RequiredArgsConstructor
public class RedisCacheConfig {

    private final LoymaxCache loymaxCache;
    @Value("${cache.ttl}")
    private Long cacheTtl;

    @Bean
    @ConditionalOnProperty(name = "cache.local", havingValue = "false")
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(cacheTtl))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    @ConditionalOnProperty(name = "cache.local", havingValue = "false")
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder -> builder
                // loymax
                .withCacheConfiguration(ACCOUNT_CACHE, getCacheConfig(ACCOUNT_CACHE))
                .withCacheConfiguration(CARDS_CACHE, getCacheConfig(CARDS_CACHE))
                .withCacheConfiguration(CHECK_CACHE, getCacheConfig(CHECK_CACHE))
                .withCacheConfiguration(FAVORITE_CATEGORIES_CACHE, getCacheConfig(FAVORITE_CATEGORIES_CACHE))
                .withCacheConfiguration(PERSONAL_OFFERS_CACHE, getCacheConfig(PERSONAL_OFFERS_CACHE))
                .withCacheConfiguration(PERSONAL_GOODS_CACHE, getCacheConfig(PERSONAL_GOODS_CACHE))
                .withCacheConfiguration(OFFERS_CACHE, getCacheConfig(OFFERS_CACHE))
                .withCacheConfiguration(STATUS_CACHE, getCacheConfig(STATUS_CACHE))
                .withCacheConfiguration(MOBILE_MAIN, getCacheConfig(MOBILE_MAIN))
                .withCacheConfiguration(USER_CACHE, getCacheConfig(USER_CACHE))
                // engine
                .withCacheConfiguration(CacheConstants.MOBILE_MAIN_COUPONS, getCacheConfig(MOBILE_MAIN))
                .withCacheConfiguration(CacheConstants.MOBILE_MAIN_SELECT_CARDS, getCacheConfig(MOBILE_MAIN))
                .withCacheConfiguration(CacheConstants.MOBILE_MAIN_CATEGORIES, getCacheConfig(MOBILE_MAIN))
                .withCacheConfiguration(CacheConstants.MOBILE_MAIN_CATALOGS, getCacheConfig(MOBILE_MAIN))
                .withCacheConfiguration(CacheConstants.MOBILE_MAIN_GOODS, getCacheConfig(MOBILE_MAIN))
                .withCacheConfiguration(CacheConstants.MOBILE_MAIN_OFFERS, getCacheConfig(MOBILE_MAIN))
                .withCacheConfiguration(CacheConstants.MOBILE_MAIN_PERS_GOODS, getCacheConfig(MOBILE_MAIN))
                .withCacheConfiguration(CacheConstants.MOBILE_MAIN_PERS_OFFERS, getCacheConfig(MOBILE_MAIN)));
    }

    private RedisCacheConfiguration getCacheConfig(String cacheName) {
        return getCacheConfig(loymaxCache.getCacheTtl(cacheName));
    }

    private RedisCacheConfiguration getCacheConfig(Long cacheTtl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cacheTtl))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()));
    }
}
