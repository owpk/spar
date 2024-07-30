package ru.sparural.notification.service.impl.firebase.settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Vorobyev Vyacheslav
 */
public class ClientSettingsUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static InputStream getSettingsJson(ClientSettingsBean dto) throws IOException {
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(dto));
    }
}