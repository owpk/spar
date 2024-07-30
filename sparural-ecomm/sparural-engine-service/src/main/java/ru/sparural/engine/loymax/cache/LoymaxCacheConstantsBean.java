package ru.sparural.engine.loymax.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
public class LoymaxCacheConstantsBean implements LoymaxCache {

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
    public static final String ADMIN_TOKEN = "adminToken";

    public Map<String, Long> cacheEntries;
    public Long DEFAULT_RECORD_TTL_IN_SECONDS;
    @Value("${loymax_cache.default_ttl}")
    private Long defaultTtl;
    @Value("${loymax_cache.cards}")
    private Long cards;
    @Value("${loymax_cache.accounts}")
    private Long accounts;
    @Value("${loymax_cache.status}")
    private Long status;
    @Value("${loymax_cache.check}")
    private Long check;
    @Value("${loymax_cache.favorite_categories}")
    private Long favoriteCategories;
    @Value("${loymax_cache.offers}")
    private Long offers;
    @Value("${loymax_cache.personal_offers}")
    private Long personalOffers;
    @Value("${loymax_cache.personal_goods}")
    private Long personalGoods;
    @Value("${loymax_cache.user}")
    private Long user;
    @Value("${loymax_cache.admin_token}")
    private Long adminToken;

    @PostConstruct
    private void init() {
        DEFAULT_RECORD_TTL_IN_SECONDS = defaultTtl;
        cacheEntries = Map.of(
                CARDS_CACHE, cards,
                ACCOUNT_CACHE, accounts,
                STATUS_CACHE, status,
                CHECK_CACHE, check,
                FAVORITE_CATEGORIES_CACHE, favoriteCategories,
                OFFERS_CACHE, offers,
                PERSONAL_GOODS_CACHE, personalGoods,
                PERSONAL_OFFERS_CACHE, personalOffers,
                USER_CACHE, user,
                ADMIN_TOKEN, adminToken
        );
    }

    @Override
    public Long cardsCache() {
        return cards;
    }

    @Override
    public Long account() {
        return accounts;
    }

    @Override
    public Long status() {
        return status;
    }

    @Override
    public Long check() {
        return check;
    }

    @Override
    public Long favoriteCategories() {
        return favoriteCategories;
    }

    @Override
    public Long offers() {
        return offers;
    }

    @Override
    public Long personalOffers() {
        return personalOffers;
    }

    @Override
    public Long personalGoods() {
        return personalGoods;
    }

    @Override
    public Long user() {
        return user;
    }

    @Override
    public Long getCacheTtl(String cacheName) {
        var val = cacheEntries.get(cacheName);
        return val == null ? DEFAULT_RECORD_TTL_IN_SECONDS : val;
    }
}