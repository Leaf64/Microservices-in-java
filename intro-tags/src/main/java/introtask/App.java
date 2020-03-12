package introtask;

import com.mongodb.MongoClient;
import introtask.auth.UserAuth;
import introtask.config.Config;
import introtask.config.Credentials;
import introtask.dao.TagDao;
import introtask.logic.TagLogic;
import introtask.resource.TagResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;

public class App extends Application<Config> {

    private Datastore datastore;

    public static void main(String[] args) throws Exception {
        new App().run(args);

    }

    @Override
    public String getName() {
        return "service-tags";
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(Config configuration, Environment environment) throws UnknownHostException {

        Morphia morphia = createAndConfigureMorphia();
        datastore = createAndConfigureDataStore(morphia);

        TagDao userDao = new TagDao(datastore);
        TagLogic userLogic = new TagLogic(userDao);
        final TagResource resource = new TagResource(userLogic);

        Credentials credentials = configuration.getCredentials();

        environment.jersey().register(resource);
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<UserAuth>()
                        .setAuthenticator(new Auth(credentials.getLogin(), credentials.getPassword()))
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()));
    }

    private Morphia createAndConfigureMorphia() {
        Morphia morphia = new Morphia();
        morphia.mapPackage("introtask.entity.UserTagEntity");
        return morphia;
    }

    private Datastore createAndConfigureDataStore(Morphia morphia) throws UnknownHostException {
        Datastore datastore = morphia.createDatastore(new MongoClient(), "introdb");
        datastore.ensureIndexes();
        return datastore;
    }

}
