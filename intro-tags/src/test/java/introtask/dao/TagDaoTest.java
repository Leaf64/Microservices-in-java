package introtask.dao;

import com.github.fakemongo.Fongo;
import com.vividsolutions.jts.util.Assert;
import introtask.entity.Tag;
import introtask.entity.UserTagEntity;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.UUID;

public class TagDaoTest {

    private TagDao tagDao;
    private Datastore datastore;

    @Before
    public void init(){
        Fongo fongo = new Fongo("tags-test");
        Morphia morphia = new Morphia();
        morphia.mapPackage("introtask.entity.UserTagEntity");
        datastore = morphia.createDatastore(fongo.getMongo(), "tag-test");
        datastore.ensureIndexes();
        setUpDatabase();
        tagDao = new TagDao(datastore);
    }

    private void setUpDatabase() {
        datastore.save(createEntity(UUID.fromString("62fba7eb-eef8-44c1-8d33-f1499cd6145e"), "zodiac", "cancer"));
        datastore.save(createEntity(UUID.fromString("87aca7eb-ba51-44c1-5233-f1472cd6612e"), "account", "premium"));
    }

    private UserTagEntity createEntity(UUID id, String tagId, String tagValue) {
        return new UserTagEntity(id, new Tag(tagId, tagValue) );
    }

    @Test
    public void createIfEverythingIsOkey(){
        UserTagEntity tag = createEntity(UUID.fromString("62fba7eb-eef8-44c1-8d33-f1499cd6145e"), "zodiac", "taurus");
        tagDao.create(tag);

    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotCreateIfIdIsBad(){
        UserTagEntity tag = createEntity(UUID.fromString("xd"), "zodiac", "taurus");
        tagDao.create(tag);

    }

    @Test
    public void findTagsOfUserIfOk(){
        UserTagEntity tag1 = createEntity(UUID.fromString("f4c6b8b5-d095-43a7-aced-dfbc84775959"), "zodiac", "cancer");
        datastore.save(tag1);
        UserTagEntity tag2 =createEntity(UUID.fromString("2d36e09c-d4e3-45c5-a3d1-cd9ee3a7e8f3"), "account", "premium");
        datastore.save(tag2);
        tagDao.getTagsByClientId(tag1,0,0);
    }

    @Test
    public void doNotFindIfNotExist(){
        UserTagEntity tag1 = createEntity(UUID.fromString("1b6a1cdf-df9e-4c3e-b1b0-cede45f7ff6c"), "not", "saved");
        List<UserTagEntity> list = tagDao.getTagsByClientId(tag1,0,0);
        Assert.equals(list.size(), 0);
    }

}
