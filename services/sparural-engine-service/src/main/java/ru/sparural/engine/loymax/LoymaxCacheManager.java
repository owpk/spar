package ru.sparural.engine.loymax;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Vorobyev Vyacheslav
 */
public class LoymaxCacheManager extends ConcurrentMapCacheManager {
    public static final String CARDS_CACHE = "cards";
    public static final String ACCOUNT_CACHE = "accounts";
    public static final String STATUS_CACHE = "status";
    public static final String CHECK_CACHE = "check";
    public static final String FAVORITE_CATEGORIES_CACHE = "favoriteCategories";
    public static final String OFFERS_CACHE = "offers";
    public static final String PERSONAL_OFFERS_CACHE = "personalOffers";
    public static final String PERSONAL_GOODS_CACHE = "personalGoods";
    public static final String USER_CACHE = "user";
    public static final String MOBILE_MAIN = "mobileMain";
    private static final Long DEFAULT_RECORD_TTL = TimeUnit.MINUTES.toSeconds(20);
    private static final Map<String, Long> cacheEntries;

    static {
        cacheEntries = Map.of(
                CARDS_CACHE, 10800L,
                ACCOUNT_CACHE, 10800L,
                STATUS_CACHE, 90L,
                CHECK_CACHE, 90L,
                FAVORITE_CATEGORIES_CACHE, 10800L,
                OFFERS_CACHE, 43200L,
                PERSONAL_GOODS_CACHE, 43200L,
                PERSONAL_OFFERS_CACHE, 43200L,
                USER_CACHE, 43200L,
                MOBILE_MAIN, 43200L
        );
    }

    @Override
    protected Cache createConcurrentMapCache(String name) {
        Long cacheEntryTtl = cacheEntries.get(name);
        Long ttl = cacheEntryTtl == null ? DEFAULT_RECORD_TTL : cacheEntryTtl;
        return new ConcurrentMapCache(
                name,
                CacheBuilder.newBuilder()
                        .expireAfterWrite(ttl, TimeUnit.SECONDS)
                        .build().asMap(),
                false);
    }

}