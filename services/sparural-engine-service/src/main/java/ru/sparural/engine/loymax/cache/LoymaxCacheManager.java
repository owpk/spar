package ru.sparural.engine.loymax.cache;

import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
public class LoymaxCacheManager extends ConcurrentMapCacheManager {
    private static final Long DEFAULT_RECORD_TTL = TimeUnit.MINUTES.toSeconds(20);
    private final LoymaxCache loymaxCache;

    @Override
    protected Cache createConcurrentMapCache(String name) {
        Long cacheEntryTtl = loymaxCache.getCacheTtl(name);
        Long ttl = cacheEntryTtl == null ? DEFAULT_RECORD_TTL : cacheEntryTtl;
        return new ConcurrentMapCache(
                name,
                CacheBuilder.newBuilder()
                        .expireAfterWrite(ttl, TimeUnit.SECONDS)
                        .build().asMap(),
                false);
    }

}