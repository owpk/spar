package ru.sparural.engine.api.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FileDocumentUsers {
    PERSONAL_OFFER("personalOfferId", "userId", "personal_offer_user"),
    USER_REQUEST("userId"),
    USER("id");

    private static final Map<String, FileDocumentUsers> byNameMap = Stream.of(FileDocumentUsers.values())
            .collect(Collectors.toMap(FileDocumentUsers::name, v -> v));
    private final String documentIdField;
    private final String userIdField;
    private final String table;

    private FileDocumentUsers(String userIdField) {
        this.userIdField = userIdField;
        this.documentIdField = null;
        this.table = null;
    }


    private FileDocumentUsers(String documentIdField, String userIdField, String table) {
        this.documentIdField = documentIdField;
        this.userIdField = userIdField;
        this.table = table;
    }

    public static FileDocumentUsers of(String name) {
        return byNameMap.get(name);
    }

    public String getDocumentIdField() {
        return documentIdField;
    }

    public String getUserIdField() {
        return userIdField;
    }

    public String getTable() {
        return table;
    }
}
