package com.lyc.learn.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * 全局 JSON 转换工具类（基于 Jackson，兼容 JDK 1.8）
 */
public final class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 可选：忽略 null 字段
        // OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JsonUtil() {}

    public static String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转 JSON 失败", e);
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        if (isEmpty(json)) return null;
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON 转对象失败, class = {}", clazz, e);
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    public static <T> T toGenericObject(String json, TypeReference<T> typeReference) {
        if (isEmpty(json)) return null;
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON 转泛型对象失败", e);
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    /**
     * JSON 数组转 List（兼容 JDK 1.8 写法，不使用 var）
     */
    public static <T> List<T> toList(String json, Class<T> elementClazz) {
        if (isEmpty(json)) return null;
        try {
            JavaType collectionType = OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, elementClazz);
            return OBJECT_MAPPER.readValue(json, collectionType);
        } catch (JsonProcessingException e) {
            log.error("JSON 转 List<{}> 失败", elementClazz.getSimpleName(), e);
            throw new RuntimeException("JSON 反序列化为 List 失败", e);
        }
    }

    /**
     * JSON 转 Map<String, Object>
     */
    public static Map<String, Object> toMap(String json) {
        if (isEmpty(json)) return null;
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON 转 Map 失败", e);
            throw new RuntimeException("JSON 反序列化为 Map 失败", e);
        }
    }

    /**
     * JSON 转泛型 Map（可指定 key/value 类型）
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (isEmpty(json)) return null;
        try {
            JavaType mapType = OBJECT_MAPPER.getTypeFactory()
                    .constructMapType(Map.class, keyClass, valueClass);
            return OBJECT_MAPPER.readValue(json, mapType);
        } catch (JsonProcessingException e) {
            log.error("JSON 转 Map<{},{}> 失败", keyClass.getSimpleName(), valueClass.getSimpleName(), e);
            throw new RuntimeException("JSON 反序列化为 Map 失败", e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        if (isEmpty(json)) return null;
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("JSON 转 JsonNode 失败", e);
            throw new RuntimeException("JSON 转 JsonNode 失败", e);
        }
    }

    public static JsonNode toJsonNode(Object obj) {
        if (obj == null) return null;
        return OBJECT_MAPPER.valueToTree(obj);
    }

    public static <T> T updateObject(String json, T targetObject) {
        if (isEmpty(json) || targetObject == null) return targetObject;
        try {
            return OBJECT_MAPPER.readerForUpdating(targetObject).readValue(json);
        } catch (JsonProcessingException e) {
            log.error("JSON 更新对象失败", e);
            throw new RuntimeException("JSON 更新对象失败", e);
        }
    }
    
    public static <T> T obj2obj(Object sourceObj, Class<T> clazz) {
        if (sourceObj == null) return null;
        String objStr = toJson(sourceObj);
        return toObject(objStr, clazz);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
