package ru.sparural.engine.loymax.cache;

import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.concurrent.TimeUnit;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@Slf4j
public class LoymaxCacheManager extends ConcurrentMapCacheManager {

    private final LoymaxCache loymaxCache;

    @Override
    protected Cache createConcurrentMapCache(String name) {
        log.info("Creating local cache entry: " + name);
        Long cacheEntryTtl = loymaxCache.getCacheTtl(name);
        return new ConcurrentMapCache(
                name,
                CacheBuilder.newBuilder()
                        .expireAfterWrite(cacheEntryTtl, TimeUnit.SECONDS)
                        .build().asMap(),
                false);
    }

}