package ru.sparural.rest.utils;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
public class CustomCacheManager extends ConcurrentMapCacheManager {

    @Value("${cache.ttl}")
    private Long cacheTtl;

    @Override
    protected Cache createConcurrentMapCache(String name) {
        return new ConcurrentMapCache(
                name,
                CacheBuilder.newBuilder()
                        .expireAfterWrite(TimeUnit.MINUTES.toSeconds(cacheTtl), TimeUnit.SECONDS)
                        .build().asMap(),
                false);
    }
}