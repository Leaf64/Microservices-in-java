package introtask.dao;

import com.github.fakemongo.Fongo;
import introtask.entity.UserEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;


import javax.validation.constraints.Null;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.UUID;
import java.util.Date;

public class UserDaoTest {

    private UserDao userDao;
    //private UUID testID;

    @Before
    public void init() {
        Fongo fongo = new Fongo("introdb2");
        Morphia morphia = new Morphia();
        morphia.mapPackage("introtask.entity.UserEntity");
        Datastore datastore = morphia.createDatastore(fongo.getMongo(), "introdb2");
        datastore.ensureIndexes();
        userDao = new UserDao(datastore);

    }


    @Test
    public void createUserIfEverythingIsOkey() {
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "email@gmail.com", "firstName", "secondName", new Date().toString());
        userDao.save(userEntity);
        UserEntity returnUser = userDao.findById(userEntity.getUuid());
        Assert.assertEquals(userEntity.getUuid().toString(), returnUser.getUuid().toString());
        Assert.assertEquals(userEntity.getEmail(), returnUser.getEmail().toString());
        Assert.assertEquals(userEntity.getFirstname(), returnUser.getFirstname());
        Assert.assertEquals(userEntity.getLastname(), returnUser.getLastname());
        Assert.assertEquals(userEntity.getCreationDate(), returnUser.getCreationDate());
    }

    @Test(expected = BadRequestException.class)
    public void dontCreateIfEmailIsTheSame() {
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "email@gmail.com", "firstName", "secondName", new Date().toString());
        userDao.save(userEntity);

        UserEntity userEntity2 = new UserEntity(UUID.randomUUID(), "email@gmail.com", "firstName", "secondName", new Date().toString());
        userDao.save(userEntity);

    }

    @Test
    public void findByIdIfEverythingIsOk() {

        UserEntity user1 = new UserEntity(UUID.randomUUID(), "1@gmail.com", "1", "1", new Date().toString());
        userDao.save(user1);
        UserEntity founded = userDao.findById(user1.getUuid());
        Assert.assertEquals(founded.getUuid().toString(), user1.getUuid().toString());
        Assert.assertEquals(founded.getEmail(), user1.getEmail().toString());
        Assert.assertEquals(founded.getFirstname(), user1.getFirstname());
        Assert.assertEquals(founded.getLastname(), user1.getLastname());
        Assert.assertEquals(founded.getCreationDate(), user1.getCreationDate());
    }

    @Test(expected = NullPointerException.class)
    public void doNotFindIfNotExists() {
        UserEntity founded = userDao.findById(UUID.randomUUID());
        Assert.assertEquals(founded.getUuid().toString(), null);
    }

    @Test
    public void updateIfOk() {
        String name = "1";
        UserEntity user1 = new UserEntity(UUID.randomUUID(), "1233333@gmail.com", name, "1", new Date().toString());
        userDao.save(user1);
        user1.setFirstname("2");
        user1 = userDao.update(user1);
        Assert.assertNotEquals(name, user1);
    }



}
