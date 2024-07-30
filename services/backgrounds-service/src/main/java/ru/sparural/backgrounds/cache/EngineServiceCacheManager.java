package ru.sparural.backgrounds.cache;

import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EngineServiceCacheManager extends ConcurrentMapCacheManager {
    private static final Long DEFAULT_RECORD_TTL = TimeUnit.MINUTES.toSeconds(20);

    @Override
    protected Cache createConcurrentMapCache(String name) {
        Long cacheEntryTtl = EngineCacheConstants.of(name).getTimeToLife();
        Long ttl = cacheEntryTtl == null ? DEFAULT_RECORD_TTL : cacheEntryTtl;
        log.info("caching: " + name);
        return new ConcurrentMapCache(
                name,
                CacheBuilder.newBuilder()
                        .expireAfterWrite(ttl, TimeUnit.SECONDS)
                        .build().asMap(),
                false);
    }
}
