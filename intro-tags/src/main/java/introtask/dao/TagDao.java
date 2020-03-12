package introtask.dao;

import introtask.dto.Stats;
import introtask.entity.UserTagEntity;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Group;
import org.mongodb.morphia.aggregation.Projection;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TagDao extends BasicDAO<UserTagEntity, ObjectId> {

    public TagDao(Datastore ds){
        super(ds);
    }

    static final Logger LOG = LoggerFactory.getLogger(TagDao.class);

    public UserTagEntity create(UserTagEntity userTagEntity) {
        save(userTagEntity);
        return userTagEntity;
    }

    public List<UserTagEntity> getTagsByClientId(UserTagEntity entity, int limit, int offset) {
        Query<UserTagEntity> query = createQuery();

        query.field("userId").equal(entity.getUserId());

        query = applyToQuery(query, "tag.tagId", entity.getTag().getTagId(), true);
        query = applyToQuery(query, "tag.value", entity.getTag().getValue(), true);

        query.order("_id");

        return query.asList(new FindOptions().limit(limit).skip(offset));
    }

    private Query<UserTagEntity> applyToQuery(Query<UserTagEntity> query, String fieldName, String fieldValue, boolean equal) {
        if (fieldValue != null) {
            return equal ? query.field(fieldName).equal(fieldValue) : query.field(fieldName).notEqual(fieldValue);
        }
        return query;
    }

    public Iterator<Document> getStats(int limit, int offset) {
        AggregationPipeline pipe = setPipe(limit,offset);
        Iterator<Document> result = pipe.aggregate(Document.class);
        return result;

    }

    public AggregationPipeline setPipe(int limit, int offset){

        AggregationPipeline pipe = ds.createAggregation(UserTagEntity.class)
                .group("userId", Group.grouping("amount", new Accumulator("$sum", 1)));

        pipe.skip(offset)
                .limit(limit);

        return pipe;
    }
}
