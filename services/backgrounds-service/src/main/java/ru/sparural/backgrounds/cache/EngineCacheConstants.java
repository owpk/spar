package ru.sparural.backgrounds.cache;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum EngineCacheConstants {
    LOYMAX_USERS(CacheNames.LOYMAX_USERS_CACHE, 25000L),
    SPAR_USERS(CacheNames.SPAR_USERS_CACHE, 25000L);

    public static Map<String, EngineCacheConstants> map = Arrays.stream(EngineCacheConstants.values())
            .collect(Collectors.toMap(e -> e.name, Function.identity()));

    @Getter
    private final Long timeToLife;
    @Getter
    private final String name;

    EngineCacheConstants(String name, Long ttl) {
        this.timeToLife = ttl;
        this.name = name;
    }

    public static EngineCacheConstants of(String name) {
        if (!map.containsKey(name))
            throw new RuntimeException("No cache name present: " + name);
        return map.get(name);
    }
}
