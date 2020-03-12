package dao;


import entity.UserEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class UserDao extends BasicDAO<UserEntity, ObjectId> {

    private static final String ID = "uuid";
    private static final String EMAIL = "email";

    // static final Logger LOG = LoggerFactory.getLogger(UserDao.class);
    public UserDao(Datastore ds) {
        super(ds);
    }

}
