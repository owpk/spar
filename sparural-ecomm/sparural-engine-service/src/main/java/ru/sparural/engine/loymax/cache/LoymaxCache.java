package ru.sparural.engine.loymax.cache;

/**
 * @author Vorobyev Vyacheslav
 */
public interface LoymaxCache {

    Long cardsCache();

    Long account();

    Long status();

    Long check();

    Long favoriteCategories();

    Long offers();

    Long personalOffers();

    Long personalGoods();

    Long user();

    Long getCacheTtl(String cacheName);
}
