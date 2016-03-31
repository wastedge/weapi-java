package com.wastedge.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class ApiUtils {
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new InheritableThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat;
        }
    };

    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMAT = new InheritableThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat;
        }
    };

    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_TZ_FORMAT = new InheritableThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat;
        }
    };

    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_TZ_PRINT_FORMAT = new InheritableThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat;
        }
    };

    public static Date parseDate(String value) throws ApiException {
        if (value == null) {
            return null;
        }

        try {
            return DATE_FORMAT.get().parse(value);
        } catch (ParseException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public static Date parseDateTime(String value) throws ApiException {
        if (value == null) {
            return null;
        }

        try {
            return DATE_TIME_FORMAT.get().parse(value);
        } catch (ParseException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public static Calendar parseDateTimeOffset(String value) throws ApiException {
        if (value == null) {
            return null;
        }

        try {
            SimpleDateFormat simpleDateFormat = DATE_TIME_TZ_FORMAT.get();
            Calendar calendar = Calendar.getInstance();
            simpleDateFormat.setCalendar(calendar);
            simpleDateFormat.parse(value);
            return calendar;
        } catch (ParseException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    public static String printDate(Date value) {
        if (value == null) {
            return null;
        }

        return DATE_FORMAT.get().format(value);
    }

    public static String printDateTime(Date value) {
        if (value == null) {
            return null;
        }

        return DATE_TIME_FORMAT.get().format(value);
    }

    public static String printDateTimeOffset(Calendar value) {
        if (value == null) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = DATE_TIME_TZ_PRINT_FORMAT.get();
        simpleDateFormat.setTimeZone(value.getTimeZone());
        return simpleDateFormat.format(value.getTime());
    }

    public static String serialize(Object value, EntityDataType dataType) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String)value;
        }
        if (value instanceof Date) {
            switch (dataType) {
                case DATE:
                    return printDate((Date)value);
                case DATE_TIME:
                    return printDateTime((Date)value);
                default:
                    throw new IllegalArgumentException("value");
            }
        }
        if (value instanceof Calendar) {
            return printDateTimeOffset((Calendar)value);
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
        if (value instanceof Date) {
            return new JsonPrimitive(printDateTime((Date)value));
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
