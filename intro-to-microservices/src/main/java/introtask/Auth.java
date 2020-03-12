package introtask;

import introtask.auth.UserAuth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

public class Auth implements Authenticator<BasicCredentials, UserAuth> {

    private String login;
    private String password;

    Auth(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public Optional<UserAuth> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if (credentials.getUsername().equals(this.login) && credentials.getPassword().equals(this.password)) {
            return Optional.of(new UserAuth(credentials.getUsername()));
        }
        return Optional.empty();
    }

}
