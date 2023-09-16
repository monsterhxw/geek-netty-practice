package com.github.monsterhxw.util;

import com.google.gson.Gson;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    private static final Gson GSON = new Gson();

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return GSON.fromJson(jsonStr, clazz);
    }

    public static <T> String toJson(T t) {
        return GSON.toJson(t);
    }
}
