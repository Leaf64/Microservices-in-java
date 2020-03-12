package util;

import dao.TagDao;
import dao.UserDao;
import entity.UserEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.mongodb.morphia.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserGenerator {

    Logger LOG = Logger.getLogger(getClass().getName());
    String CHARS = "ABCDEFGHIJKLMNOPRSTUWXYZabcdefghijklmnoprstuwxyz";

    UserDao userDao;

    TagGenerator tagGenerator;
    UUID currentUUID;

    public UserGenerator(UserDao userDao, TagGenerator tagGenerator) {

        this.userDao = userDao;
        this.tagGenerator = tagGenerator;
    }

    public void start(int usersAmount, int tagAmount) {
        for (int i = 0; i < usersAmount; i++) {
            //System.out.println("Generating user " + i + " of " + usersAmount);
            LOG.log(Level.INFO, "Generating user " + i + " of " + usersAmount);
            generateRandomUser(tagAmount);
        }
    }

    public void generateRandomUser(int tagAmount) {
        String firstname = RandomStringUtils.random(7, CHARS);
        String lastname = RandomStringUtils.random(7, CHARS);
        String email = RandomStringUtils.random(7, CHARS) + "@gmail.com";
        String creationDate = DateParser.dateToString(new Date());

        createUser(email, firstname, lastname, creationDate, tagAmount);
    }

    public void createUser(String email, String firstname, String lastname, String creationDate, int tagAmount) {
        UUID id = UUID.randomUUID();
       // uuids.add(id);
        currentUUID = id;
        UserEntity user = new UserEntity(id, email, firstname, lastname, creationDate);
        userDao.save(user);
        tagGenerator.start(tagAmount, id);
    }


}
