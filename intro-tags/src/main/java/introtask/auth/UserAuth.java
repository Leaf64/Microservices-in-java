package introtask.auth;

import java.security.Principal;

public class UserAuth implements Principal {

    private String login;
    private String password;

    public UserAuth(String login) {
        this.login = login;
    }

    public UserAuth(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String getName() {
        return null;
    }
}
