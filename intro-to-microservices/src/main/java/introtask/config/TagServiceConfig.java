package introtask.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TagServiceConfig {
    private Connector connector;
    private String login;
    private String password;

    @JsonProperty
    public Connector getConnector() {
        return connector;
    }
    @JsonProperty
    public void setConnector(Connector connector) {
        this.connector = connector;
    }
    @JsonProperty
    public String getLogin() {
        return login;
    }
    @JsonProperty
    public void setLogin(String login) {
        this.login = login;
    }
    @JsonProperty
    public String getPassword() {
        return password;
    }
    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUrl(){
        return connector.getType() + "://" + connector.getAddress() + ":" + connector.getPort();
    }

}
class Connector{
    private String type;
    private String address;
    private int port;

    @JsonProperty
    String getType() {
        return type;
    }
    @JsonProperty
    public void setType(String type) {
        this.type = type;
    }
    @JsonProperty
    String getAddress() {
        return address;
    }
    @JsonProperty
    public void setAddress(String address) {
        this.address = address;
    }
    @JsonProperty
    int getPort() {
        return port;
    }
    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }
}
