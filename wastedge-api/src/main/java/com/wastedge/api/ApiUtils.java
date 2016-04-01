package com.wastedge.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


class ApiUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter DATE_TIME_TZ_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").withOffsetParsed();

    public static LocalDateTime parseDate(String value) {
        if (value == null) {
            return null;
        }

        return DATE_FORMAT.parseLocalDateTime(value);
    }

    public static LocalDateTime parseDateTime(String value) {
        if (value == null) {
            return null;
        }

        return DATE_TIME_FORMAT.parseLocalDateTime(value);
    }

    public static DateTime parseDateTimeOffset(String value) {
        if (value == null) {
            return null;
        }

        return DATE_TIME_TZ_FORMAT.parseDateTime(value);
    }

    public static String printDate(LocalDateTime value) {
        if (value == null) {
            return null;
        }

        return DATE_FORMAT.print(value);
    }

    public static String printDateTime(LocalDateTime value) {
        if (value == null) {
            return null;
        }

        return DATE_TIME_FORMAT.print(value);
    }

    public static String printDateTimeOffset(DateTime value) {
        if (value == null) {
            return null;
        }

        return DATE_TIME_TZ_FORMAT.print(value);
    }

    public static String serialize(Object value, EntityDataType dataType) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String)value;
        }
        if (value instanceof DateTime) {
            switch (dataType) {
                case DATE:
                    return printDate(((DateTime)value).toLocalDateTime());
                case DATE_TIME:
                    return printDateTime(((DateTime)value).toLocalDateTime());
                case DATE_TIME_TZ:
                    return printDateTimeOffset((DateTime)value);
                default:
                    throw new IllegalArgumentException("value");
            }
        }
        if (value instanceof LocalDateTime) {
            switch (dataType) {
                case DATE:
                    return printDate((LocalDateTime)value);
                case DATE_TIME:
                    return printDateTime((LocalDateTime)value);
                case DATE_TIME_TZ:
                    return printDateTimeOffset(((LocalDateTime)value).toDateTime());
                default:
                    throw new IllegalArgumentException("value");
            }
        }
        if (value instanceof Number) {
            return value.toString();
        }

        throw new IllegalArgumentException("value");
    }

    public static JsonElement toJson(Object value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        }
        if (value instanceof String) {
            return new JsonPrimitive((String)value);
        }
        if (value instanceof Number) {
            return new JsonPrimitive((Number)value);
        }
        if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean)value);
        }
        if (value instanceof LocalDateTime) {
            return new JsonPrimitive(printDateTime((LocalDateTime)value));
        }
        if (value instanceof DateTime) {
            return new JsonPrimitive(printDateTimeOffset((DateTime)value));
        }

        throw new IllegalArgumentException("value");
    }

    public static Object fromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = (JsonPrimitive)element;
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
            if (primitive.isString()) {
                return primitive.getAsString();
            }
            return primitive.getAsNumber();
        }
        throw new IllegalArgumentException("element");
    }
}
