package com.wastedge.api;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Api {
    private Schema schema;
    private final Gson gson = new Gson();
    private final Map<String, EntitySchema> entities = new HashMap<>();

    private ApiCredentials credentials;

    public Api(ApiCredentials credentials) {
        Validate.notNull(credentials, "credentials");

        this.credentials = credentials;
    }

    public ApiCredentials getCredentials() {
        return credentials;
    }

    public Schema getSchema() throws IOException {
        if (schema == null) {
            schema = new Schema((JsonObject)executeJson(null, "$meta", "GET", null));
        }

        return schema;
    }

    public EntitySchema getEntitySchema(String name) throws IOException {
        Validate.notNull(name, "name");

        EntitySchema schema = entities.get(name);
        if (schema == null) {
            schema = new EntitySchema(name, (JsonObject)executeJson(name, "$meta", "GET", null));
            entities.put(name, schema);
        }

        return schema;
    }

    public ApiQuery createQuery(EntitySchema entity) {
        return new ApiQuery(this, entity);
    }

    public ApiUpdate createCreate(EntitySchema entity) {
        return new ApiUpdate(this, entity, null, ApiUpdateMode.CREATE);
    }

    public ApiUpdate createCreate(EntitySchema entity, RecordSet recordSet) {
        return new ApiUpdate(this, entity, recordSet, ApiUpdateMode.CREATE);
    }

    public ApiUpdate createUpdate(EntitySchema entity) {
        return new ApiUpdate(this, entity, null, ApiUpdateMode.UPDATE);
    }

    public ApiUpdate createUpdate(EntitySchema entity, RecordSet recordSet) {
        return new ApiUpdate(this, entity, recordSet, ApiUpdateMode.UPDATE);
    }

    public ApiDelete createDelete(EntitySchema entity) {
        return new ApiDelete(this, entity, null);
    }

    public ApiDelete createDelete(EntitySchema entity, List<String> ids) {
        return new ApiDelete(this, entity, ids);
    }

    JsonElement executeJson(String path, String parameters, String method, final JsonElement request) throws IOException {
        return execute(
            path,
            parameters,
            method,
            new Serializer<JsonElement>() {
                @Override
                public void write(OutputStream stream) throws IOException {
                    if (request != null) {
                        try (
                            Writer writer = new OutputStreamWriter(stream);
                            JsonWriter jsonWriter = new JsonWriter(writer)
                        ) {
                            gson.toJson(request, jsonWriter);
                        }
                    }
                }

                @Override
                public JsonElement read(InputStream stream) throws IOException {
                    try (Reader reader = new InputStreamReader(stream)) {
                        return new JsonParser().parse(reader);
                    }
                }
            }
        );
    }

    public String executeRaw(String path, String parameters, String method, final String request) throws IOException {
        return execute(
            path,
            parameters,
            method,
            new Serializer<String>() {
                @Override
                public void write(OutputStream stream) throws IOException {
                    if (request != null) {
                        try (Writer writer = new OutputStreamWriter(stream)) {
                            writer.write(request);
                        }
                    }
                }

                @Override
                public String read(InputStream stream) throws IOException {
                    return IOUtils.toString(stream);
                }
            }
        );
    }

    <T> T execute(String path, String parameters, String method, Serializer<T> serializer) throws IOException {
        Validate.notNull(method, "method");

        HttpURLConnection connection = buildRequest(path, parameters, method);

        // TODO: Once we support property verbs, this needs to exclude DELETE too.

        if (!"GET".equals(method)) {
            connection.setDoOutput(true);

            try (OutputStream stream = connection.getOutputStream()){
                serializer.write(stream);
            }
        }

        if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 299) {
            try (InputStream stream = getResponseStream(connection, connection.getInputStream())) {
                return serializer.read(stream);
            }
        }

        try (
            InputStream stream = getResponseStream(connection, connection.getErrorStream());
            InputStream bufferedStream = new BufferedInputStream(stream);
            Reader reader = new InputStreamReader(bufferedStream)
        ) {
            throw parseError(reader);
        }
    }

    private ApiException parseError(Reader error) {
        JsonObject object = null;

        try {
            object = new JsonParser().parse(error).getAsJsonObject();
        } catch (Throwable e) {
            // If we cannot parse the error response, we're just rethrowing the
            // original exception.

            return new ApiException("Unknown error");
        }

        // First check whether it's a simple response.

        if (object.has("message")) {
            JsonElement message = object.get("message");

            if (message.isJsonPrimitive() && message.getAsJsonPrimitive().isString()) {
                return new ApiException(message.getAsString());
            }
            if (message.isJsonArray()) {
                return new ApiException(message.getAsJsonArray().get(0).getAsString());
            }

            // If we can't make sense of the response, throw the original exception.

            return new ApiException("Unknown error");
        }

        // Next, see whether we got an error collection.

        if (object.has("errors") && object.get("errors").isJsonArray()) {
            List<ApiRowErrors> errors = new ArrayList<>();

            for (JsonElement element : object.get("errors").getAsJsonArray()) {
                errors.add(ApiRowErrors.fromJson(element.getAsJsonObject()));
            }

            return new ApiException("Validation failed", errors);
        }

        // If all else fails, rethrow the original exception.

        return new ApiException("Unknown error");
    }

    private InputStream getResponseStream(HttpURLConnection connection, InputStream stream) throws IOException {
        if ("gzip".equalsIgnoreCase(connection.getHeaderField("Content-Encoding"))) {
            return new GZIPInputStream(stream);
        }

        return stream;
    }

    private HttpURLConnection buildRequest(String path, String parameters, String method) throws IOException {
        StringBuilder url = new StringBuilder();

        url.append(credentials.getUrl());
        if (!credentials.getUrl().endsWith("/")) {
            url.append('/');
        }

        url.append("api/rest");
        if (!StringUtils.isEmpty(path)) {
            url.append('/').append(path);
        }

        // This is a work around because webspeed doesn't accept PUT or DELETE.

        if (!"GET".equals(method) && !"POST".equals(method)) {
            if (parameters != null) {
                parameters += "&";
            } else {
                parameters = "";
            }

            parameters += "$method=" + method;
            method = "POST";
        }

        if (parameters != null) {
            url.append('?').append(parameters);
        }

        HttpURLConnection connection = (HttpURLConnection)new URL(url.toString()).openConnection();

        connection.setRequestMethod(method);

        byte[] authorization = (credentials.getCompany() + "\\" + credentials.getUsername() + ":" + credentials.getPassword()).getBytes(StandardCharsets.UTF_8);

        connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String(authorization));

        return connection;
    }

    private static interface Serializer<T> {
        void write(OutputStream stream) throws IOException;

        T read(InputStream stream) throws IOException;
    }
}
