package ru.sparural.utils.rest;

import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public class RestResponse {
    private final int code;
    private final String msg;
    private final byte[] body;

    @Setter
    private Map<String, List<String>> headers;

    RestResponse(int code, String msg, byte[] body) {
        headers = new HashMap<>();
        this.code = code;
        this.msg = msg;
        this.body = body;
    }

    public void addHeader(String key, String value) {
        headers.put(key, List.of(value));
    }

    RestResponse(int code, String msg) {
        this(code, msg, new byte[0]);
    }

    @Override
    public String toString() {
        return String.format("{ \"code\" : \"%d\", \"message\" : \"%s\", \"body\" : \"%s\"",
                code, msg, new String(body, StandardCharsets.UTF_8));
    }
}