package util;

import dao.TagDao;
import entity.Tag;
import entity.UserTagEntity;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagGenerator {
    private TagDao dao;
    String CHARS = "ABCDEFGHIJKLMNOPRSTUWXYZabcdefghijklmnoprstuwxyz";
    String NUMS = "1234567890";

    Logger LOG = Logger.getLogger(getClass().getName());

    public TagGenerator(TagDao dao) {
        this.dao = dao;
    }

    public void start(int max, UUID uuid) {
            int tagsAmount = random(max);
            //System.out.println("Generating" + tagsAmount + " tags for user: " + id);
            //W zasadzie tutaj się zastanawiam czy uzywac loggera czy po prostu printa, bo to skrypt ktory obsluguje sie z konsoli
            LOG.log(Level.INFO, "Generating " + tagsAmount + " tags for user: " + uuid);
            for (int i = 0; i < tagsAmount; i++) {
                generateTag(uuid);
            }
    }

    private void generateTag(UUID id) {
        String tagId = RandomStringUtils.random(6, NUMS);
        String value = RandomStringUtils.random(5, CHARS);
        Tag tag = new Tag(tagId, value);
        UserTagEntity userTag = new UserTagEntity(id, tag);
        dao.save(userTag);
    }

    private int random(int max) {
        return ThreadLocalRandom.current().nextInt(0, max + 1);
    }

}
