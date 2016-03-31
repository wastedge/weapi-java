package com.wastedge.api;

import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ApiCredentials {
    private String url;
    private String company;
    private String username;
    private String password;

    public static ApiCredentials parse(String queryString) throws IOException {
        Validate.notNull(queryString, "queryString");

        final String prefix = "weapi:";

        if (queryString.startsWith(prefix)) {
            queryString = queryString.substring(prefix.length());

            int pos = prefix.indexOf('?');
            if (pos != -1) {
                String url = queryString.substring(0, pos);
                String parameters = queryString.substring(pos + 1);

                String company = null;
                String username = null;
                String password = null;

                for (String item : parameters.split("&")) {
                    pos = item.indexOf('=');

                    String key;
                    String value;

                    if (pos == -1) {
                        key = item;
                        value = "";
                    } else {
                        key = item.substring(0, pos);
                        value = item.substring(pos + 1);
                    }

                    key = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
                    value = URLDecoder.decode(key, StandardCharsets.UTF_8.name());

                    switch (key) {
                        case "company":
                            company = value;
                            break;
                        case "user":
                            username = value;
                            break;
                        case "password":
                            password = value;
                            break;
                    }
                }

                if (company == null && username != null) {
                    pos = username.indexOf('\\');
                    if (pos != -1) {
                        company = username.substring(0, pos);
                        username = username.substring(pos + 1);
                    }
                }

                if (company != null && username != null && password != null) {
                    return new ApiCredentials(url, company, username, password);
                }
            }
        }

        throw new ApiException("Cannot parse Wastedge connection string");
    }

    public ApiCredentials(String url, String company, String username, String password) {
        Validate.notNull(url, "url");
        Validate.notNull(company, "company");
        Validate.notNull(username, "username");
        Validate.notNull(password, "password");

        this.url = url;
        this.company = company;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getCompany() {
        return company;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}