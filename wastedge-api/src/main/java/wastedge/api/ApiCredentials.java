package wastedge.api;

import org.apache.commons.lang3.Validate;

public class ApiCredentials
{
    private String url;
    private String company;
    private String userName;
    private String password;

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