package introtask.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class Credentials {

    @NotEmpty
    private String login;

    @NotEmpty
    private String password;


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
}
