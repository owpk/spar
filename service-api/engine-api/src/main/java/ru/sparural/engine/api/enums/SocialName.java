package ru.sparural.engine.api.enums;

import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum SocialName {
    FACEBOOK("facebook"),
    VKONTAKTE("vk"),
    APPLE("apple"),
    ODNOKLASSNIKI("odnoklassniki");

    static final Map<String, SocialName> socialNamesMap;

    static {
        socialNamesMap = Stream.of(SocialName.values())
                .collect(Collectors.toMap(SocialName::getName, x -> x));
    }

    private final String name;

    SocialName(String name) {
        this.name = name;
    }

    public static Optional<SocialName> of(String socialName) {
        return Optional.ofNullable(socialNamesMap.get(socialName));
    }
}
