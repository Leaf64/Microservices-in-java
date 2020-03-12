package introtask;

import com.mongodb.MongoClient;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import introtask.auth.UserAuth;
import introtask.config.Config;
import introtask.config.Credentials;
import introtask.config.JobIntervals;
import introtask.config.TagServiceConfig;
import introtask.external.TagsUser;
import introtask.dao.UserDao;
import introtask.job.BirthdayJob;
import introtask.job.ZodiacJob;
import introtask.resource.UserResource;
import introtask.logic.UserLogic;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.net.UnknownHostException;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class App extends Application<Config> {

    private Datastore datastore;
    private UserDao userDao;
    private TagsUser tagsUser;
    private UserLogic userLogic;
    public static void main(String[] args) throws Exception {
        new App().run(args);

    }

    @Override
    public String getName() {
        return "intro-to-microservices";
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(Config configuration, Environment environment) throws UnknownHostException, SchedulerException {

        Morphia morphia = createAndConfigureMorphia();

        datastore = createAndConfigureDataStore(morphia);

        TagServiceConfig tagServiceConfig = configuration.getTagServiceConfig();
        tagsUser = createAndConfigureTagsUser(tagServiceConfig);

        userDao = new UserDao(datastore);

        userLogic = new UserLogic(userDao, tagsUser);
        final UserResource resource = new UserResource(userLogic, userDao);

        Credentials credentials = configuration.getCredentials();

        environment.jersey().register(resource);
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<UserAuth>()
                        .setAuthenticator(new Auth(credentials.getLogin(), credentials.getPassword()))
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()));

        createAndConfigureScheduler(configuration.getJobIntervals());

    }

    private Morphia createAndConfigureMorphia() {
        Morphia morphia = new Morphia();
        morphia.mapPackage("introtask.entity.UserEntity");
        return morphia;
    }

    private Datastore createAndConfigureDataStore(Morphia morphia) throws UnknownHostException {
        Datastore datastore = morphia.createDatastore(new MongoClient(), "introdb");
        datastore.ensureIndexes();
        return datastore;
    }

    private TagsUser createAndConfigureTagsUser(TagServiceConfig tsconfing) {
        TagsUser tagsUser = Feign.builder()
                .client(new OkHttpClient())
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(new BasicAuthRequestInterceptor(tsconfing.getLogin(), tsconfing.getPassword()))
                .target(TagsUser.class, tsconfing.getUrl());
        return tagsUser;
    }

    private void createAndConfigureScheduler(JobIntervals jobIntervals) throws SchedulerException {

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        JobDetail jobBirthday = newJob(BirthdayJob.class).withIdentity("birthdayJob", "group1").build();
        JobDetail jobZodiac = newJob(ZodiacJob.class).withIdentity("zodiacJob", "group1").build();

        Trigger triggerBirthday = createSimpleTrigger("triggerBirthday", "group1", jobIntervals.getBirthdayJob());
        Trigger triggerZodiac = createSimpleTrigger("triggerZodiac", "group1", jobIntervals.getZodiacJob());

        scheduler.getContext().put("userDao", userDao);
        scheduler.getContext().put("userLogic", userLogic);

        scheduler.scheduleJob(jobBirthday, triggerBirthday);
        scheduler.scheduleJob(jobZodiac, triggerZodiac);
        scheduler.start();
    }

    private Trigger createSimpleTrigger(String name, String group, int interval) {

        Trigger trigger = newTrigger()
                .withIdentity(name, group)
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever())
                .build();
        return trigger;
    }
}
