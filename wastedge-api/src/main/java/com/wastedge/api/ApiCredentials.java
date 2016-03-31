package com.wastedge.api;

import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ApiCredentials {
    private String url;
    private String company;
    private String userName;
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
                String userName = null;
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
                            userName = value;
                            break;
                        case "password":
                            password = value;
                            break;
                    }
                }

                if (company == null && userName != null) {
                    pos = userName.indexOf('\\');
                    if (pos != -1) {
                        company = userName.substring(0, pos);
                        userName = userName.substring(pos + 1);
                    }
                }

                if (company != null && userName != null && password != null) {
                    return new ApiCredentials(url, company, userName, password);
                }
            }
        }

        throw new ApiException("Cannot parse Wastedge connection string");
    }

    public ApiCredentials(String url, String company, String userName, String password) {
        Validate.notNull(url, "url");
        Validate.notNull(company, "company");
        Validate.notNull(userName, "userName");
        Validate.notNull(password, "password");

        this.url = url;
        this.company = company;
        this.userName = userName;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getCompany() {
        return company;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}