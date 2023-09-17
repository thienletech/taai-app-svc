package com.taai.app.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unused")
public class JsonUtil {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private JsonUtil() {
    }

    static {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> type) throws JsonProcessingException {
        return jsonMapper.readValue(json, type);
    }
}
