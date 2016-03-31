package wastedge.api.jasper.designer;

@SuppressWarnings("unused")
public class WastedgeConnectionManager {
    private com.wastedge.api.jdbc.WastedgeConnection connection;
    private String url;
    private String company;
    private String username;
    private String password;

    public void returnConnection(com.wastedge.api.jdbc.WastedgeConnection connection) {
        this.connection = connection;
    }

    public com.wastedge.api.jdbc.WastedgeConnection borrowConnection() {
        com.wastedge.api.jdbc.WastedgeConnection connection = this.connection;
        this.connection = null;
        return connection;
    }

    public void shutdown() {
        // Ignore.
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
