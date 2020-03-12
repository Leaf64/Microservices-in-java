package introtask.dao;

import introtask.entity.UserEntity;
import introtask.logic.UserLogic;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static introtask.error.ErrorMessage.USER_ALREADY_EXISTS;
import static java.lang.String.format;

public class UserDao extends BasicDAO<UserEntity, ObjectId> {

    private static final String ID = "uuid";
    private static final String EMAIL = "email";
    static final Logger LOG = LoggerFactory.getLogger(UserDao.class);
    public UserDao(Datastore ds) {
        super(ds);
    }

    public UserEntity findById(UUID uuid) {
        return createQuery().field(ID).equal(uuid).get();
    }


    public UserEntity findByEmail(String email) {
        return createQuery().field(EMAIL).equal(email).get();
    }

    public List<UserEntity> findUsers(Optional<UUID> id,
                                      Optional<String> email,
                                      Optional<String> firstName,
                                      Optional<String> lastName,
                                      Optional<Integer> offset,
                                      Optional<Integer> limit,
                                      Optional<String> order) {

        Query<UserEntity> query = createQuery();

        id.ifPresent(uuid -> query.filter("uuid ==", uuid));
        email.ifPresent(s -> query.filter("email ==", s));
        firstName.ifPresent(s -> query.filter("firstName ==", s));
        lastName.ifPresent(s -> query.filter("lastName ==", s));

        offset.ifPresent(query::offset);
        limit.ifPresent(query::limit);
        order.ifPresent(query::order);

        return query.asList();
    }

    public List<UserEntity> findUsersWithBirthday(Optional<Integer> offset,
                                                     Optional<Integer> limit,
                                                     Optional<String> order){
        Query<UserEntity> query = createQuery();
        offset.ifPresent(query::offset);
        limit.ifPresent(query::limit);
        order.ifPresent(query::order);

        query.and(
                //query.criteria("dateOfBirth").exists(),
                query.criteria("dateOfBirth").notEqual(null)
        );

        return query.asList();
    }

    public List<UserEntity> findUsersWithoutBirthday(Optional<Integer> offset,
                                                     Optional<Integer> limit,
                                                     Optional<String> order){
        Query<UserEntity> query = createQuery();
        offset.ifPresent(query::offset);
        limit.ifPresent(query::limit);
        order.ifPresent(query::order);

        query.and(
                query.criteria("dateOfBirth").doesNotExist(),
                query.criteria("dateOfBirth").equal(null)
        );

        return query.asList();
    }


    public UserEntity update(@Valid UserEntity userEntity) {

        Query<UserEntity> query = createQuery().field(ID).equal(userEntity.getUuid());
        UpdateOperations<UserEntity> ops = createUpdateOperations();

        ops = applyToUpdateQuery(ops, "firstName", userEntity.getFirstname());
        ops = applyToUpdateQuery(ops, "lastName", userEntity.getLastname());
        ops = applyToUpdateQuery(ops, "email", userEntity.getEmail());

        if(userEntity.getDateOfBirth() != null){
            ops = applyToUpdateQuery(ops, "dateOfBirth", userEntity.getDateOfBirth());
        }

        update(query,ops);
        return userEntity;
    }

    private UpdateOperations<UserEntity> applyToUpdateQuery(UpdateOperations<UserEntity> update, String fieldName, Object fieldValue) {
        if (fieldValue != null) {
            return update.set(fieldName, fieldValue);
        }
        return update;
    }
}
