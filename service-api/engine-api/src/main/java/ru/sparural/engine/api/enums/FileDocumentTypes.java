package ru.sparural.engine.api.enums;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FileDocumentTypes {
    INFO_SCREEN("infoScreen", true, false, "info_screens"),
    ONBOX_BANNER("onboxBanner", true, false, "onbox_banners"),
    USER("user", false, false, "users"),
    FAVORITE_CATEGORY("favoriteCategory", true, false, "favorite_categories"),
    COUPON_EMISSION("couponEmission", true, false, "coupon_emissions"),
    PERSONAL_OFFER("personalOffer", true, true, "personal_offers"),
    GOODS("goods", true, false, "goods"),
    CATALOG("catalog", true, false, "catalogs"),
    MERCHANT_ATTRIBUTE("merchantAttribute", true, false, "merchant_attributes"),
    MERCHANT_FORMAT("merchantFormat", true, false, "merchant_formats"),
    USER_REQUEST("userRequest", false, true, "user_requests"),
    DELIVERY("delivery", true, false, "delivery"),
    MESSAGE_TEMPLATE("messageTemplate", false, true, "messages_templates"),
    CLIENT_STATUS("clientStatus", true, false, "client_statuses"),
    OFFERS("offers", true, false, "offers"),

    RECIPE_ATTRIBUTES("recipeAttributes", true, false, "recipe_attributes"),
    RECIPE("recipes", true, false, "recipes"),

    SUPPORT_CHATS("supportChats", true, false, "support_chat_messages");

    private static final Map<String, FileDocumentTypes> typesByNameMap = Stream.of(FileDocumentTypes.values())
            .collect(Collectors.toMap(FileDocumentTypes::getName, v -> v));

    private final String name;
    private final boolean writeProtected;
    private final boolean readProtected;
    private final String table;

    FileDocumentTypes(String name, boolean writeProtected, boolean readProtected, String table) {
        this.name = name;
        this.writeProtected = writeProtected;
        this.readProtected = readProtected;
        this.table = table;
    }

    public static FileDocumentTypes of(String name) {
        return Optional.ofNullable(typesByNameMap.get(name)).orElseThrow(IllegalArgumentException::new);
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public boolean isWriteProtected() {
        return writeProtected;
    }

    public boolean isReadProtected() {
        return readProtected;
    }

}
