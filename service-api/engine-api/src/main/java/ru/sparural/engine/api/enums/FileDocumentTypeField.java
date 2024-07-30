package ru.sparural.engine.api.enums;

import java.util.List;

public enum FileDocumentTypeField {
    INFO_SCREEN_PHOTO(FileDocumentTypes.INFO_SCREEN, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    ONBOX_BANNER_PHOTO(FileDocumentTypes.ONBOX_BANNER, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    USER_PHOTO(FileDocumentTypes.USER, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    FAVORITE_CATEGORY_PHOTO(FileDocumentTypes.FAVORITE_CATEGORY, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    COUPON_EMISSION_PHOTO(FileDocumentTypes.COUPON_EMISSION, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    PERSONAL_OFFER_PREVIEW(FileDocumentTypes.PERSONAL_OFFER, "preview", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    PERSONAL_OFFER_PHOTO(FileDocumentTypes.PERSONAL_OFFER, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    GOODS_PHOTO(FileDocumentTypes.GOODS, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    GOODS_PREVIEW(FileDocumentTypes.GOODS, "preview", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    CATALOG(FileDocumentTypes.CATALOG, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    MERCHANT_ATTRIBUTE_ICON(FileDocumentTypes.MERCHANT_ATTRIBUTE, "icon", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    MERCHANT_FORMAT_ICON(FileDocumentTypes.MERCHANT_FORMAT, "icon", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    RECIPE_ATTRIBUTE_ICON(FileDocumentTypes.RECIPE_ATTRIBUTES, "icon", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    RECIPE_PREVIEW(FileDocumentTypes.RECIPE, "preview", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    RECIPE_PHOTO(FileDocumentTypes.RECIPE, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    USER_REQUEST_ATTACHMENTS(FileDocumentTypes.USER_REQUEST, "attachments", true, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    DELIVERY_PHOTO(FileDocumentTypes.DELIVERY, "photo", true, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    MESSAGE_TEMPLATE_PHOTO(FileDocumentTypes.MESSAGE_TEMPLATE, "photo", true, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    CLIENT_STATUS_ICON(FileDocumentTypes.CLIENT_STATUS, "icon", true, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    OFFER_PHOTO(FileDocumentTypes.OFFERS, "photo", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    OFFER_PREVIEW(FileDocumentTypes.OFFERS, "preview", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp")),
    SUPPORT_CHATS_MESSAGE_FILE(FileDocumentTypes.SUPPORT_CHATS, "file", false, 10485760l, List.of("image/png", "image/gif", "image/jpeg", "image/pjpeg", "image/webp"));

    private final FileDocumentTypes documentType;
    private final String name;
    private final boolean multy;
    private final Long maxSize;
    private final List<String> types;

    FileDocumentTypeField(FileDocumentTypes documentType, String field, boolean multy, Long maxSize, List<String> types) {
        this.documentType = documentType;
        this.name = field;
        this.multy = multy;
        this.maxSize = maxSize;
        this.types = types;
    }

    public FileDocumentTypes getDocumentType() {
        return documentType;
    }

    public String getName() {
        return name;
    }

    public boolean isMulty() {
        return multy;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public List<String> getTypes() {
        return types;
    }

}