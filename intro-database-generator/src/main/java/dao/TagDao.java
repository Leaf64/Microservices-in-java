package dao;


import entity.UserTagEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;


public class TagDao extends BasicDAO<UserTagEntity, ObjectId> {

    public TagDao(Datastore ds) {
        super(ds);
    }

    // static final Logger LOG = LoggerFactory.getLogger(TagDao.class);

    public UserTagEntity create(UserTagEntity userTagEntity) {
        save(userTagEntity);
        return userTagEntity;
    }
}
