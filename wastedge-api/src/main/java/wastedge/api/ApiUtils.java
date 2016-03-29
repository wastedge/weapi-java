package wastedge.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonToken;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;

class ApiUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter DATE_TIME_TZ_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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

    public static String printDate(ReadablePartial value) {
        if (value == null) {
            return null;
        }

        return DATE_FORMAT.print(value);
    }

    public static String printDateTime(ReadablePartial value) {
        if (value == null) {
            return null;
        }

        return DATE_TIME_FORMAT.print(value);
    }

    public static String printDateTimeOffset(ReadablePartial value) {
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
        if (value instanceof ReadablePartial) {
            switch (dataType) {
                case DATE:
                    return printDate((ReadablePartial)value);
                case DATE_TIME:
                    return printDateTime((ReadablePartial)value);
                case DATE_TIME_TZ:
                    return printDateTimeOffset((ReadablePartial)value);
                default:
                    throw new IllegalArgumentException("value");
            }
        }
        if (value instanceof Integer) {
            return Integer.toString((int)value);
        }
        if (value instanceof Long) {
            return Long.toString((long)value);
        }
        if (value instanceof Float) {
            return Float.toString((float)value);
        }
        if (value instanceof Double) {
            return Double.toString((double)value);
        }
        if (value instanceof BigDecimal) {
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
            return new JsonPrimitive(printDateTime((ReadablePartial)value));
        }
        if (value instanceof DateTime) {
            return new JsonPrimitive(printDateTimeOffset((ReadablePartial)value));
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
