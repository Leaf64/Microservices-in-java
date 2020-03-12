package introtask.logic;

import com.mongodb.DuplicateKeyException;
import introtask.dao.TagDao;
import introtask.dto.PaginationDto;
import introtask.dto.Stats;
import introtask.dto.UserTagsDto;
import introtask.entity.Tag;
import introtask.entity.UserTagEntity;
import introtask.resource.TagResource;
import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

import static introtask.error.ErrorMessage.TAG_ALREADY_EXISTS;
import static java.lang.String.format;

public class TagLogic {
    private TagDao tagDao;
    private ModelMapper mapper = new ModelMapper();
    static final Logger LOG = LoggerFactory.getLogger(TagResource.class);


    public TagLogic(TagDao tagDao){
        this.tagDao = tagDao;
    }

    public void create(UserTagsDto userTagsDto) {

        UUID uuid;
        try {
            uuid = UUID.fromString(userTagsDto.getUserId());
        } catch (IllegalArgumentException | NullPointerException ex) {
            LOG.error("Invalid user id in request. Rejected");
            throw new IllegalArgumentException(format("Invalid user id"));
        }

        for (Tag tag : userTagsDto.getTags()) {
            try {
                tagDao.create(new UserTagEntity(uuid, tag));
            } catch (DuplicateKeyException ex) {
                LOG.error("Duplicated key. Rejected");
                throw new BadRequestException(format(TAG_ALREADY_EXISTS));
            }
        }
    }

    public List<Tag> getTagsByUserId(String userId, Tag tag, int limit, int offset) {

        List<UserTagEntity> userTagEntities = getTagEntitiesFromDb(userId,tag,limit,offset);
        List<Tag> tags = mapEntitiesToTags(userTagEntities);
        return tags;
    }

    public HashMap<String, PaginationDto<Tag>>  getTagsForManyId(List<String> userIdList, Tag tag, int limit, int offset){

        HashMap<String, PaginationDto<Tag>> userTagsMap = new HashMap<String, PaginationDto<Tag>>();
        for(String userId : userIdList){

            List<UserTagEntity> userTagEntities = getTagEntitiesFromDb(userId,tag,limit,offset);
            List<Tag> tags = mapEntitiesToTags(userTagEntities);
            PaginationDto<Tag> pagination = packTagsToPagination(tags,offset,limit);
            userTagsMap.put(userId,pagination);
        }
        return userTagsMap;
    }

    private List<UserTagEntity> getTagEntitiesFromDb(String userId, Tag tag, int limit, int offset){
        UserTagEntity queryEntity = createEntityForQuery(userId, tag);
        List<UserTagEntity> userTagEntities = tagDao.getTagsByClientId(queryEntity,limit,offset);
        return userTagEntities;
    }

    private UserTagEntity createEntityForQuery(String userId, Tag tag){

       return new UserTagEntity(UUID.fromString(userId),tag);
    }

    private List<Tag> mapEntitiesToTags(List<UserTagEntity> userTagEntities){
        return userTagEntities.stream().map(entity -> mapper.map(entity.getTag(), Tag.class)).collect(Collectors.toList());
    }

    private PaginationDto<Tag> packTagsToPagination(List<Tag> tags, int offset, int limit){
        PaginationDto.Pagination pagination = new PaginationDto.Pagination(offset,limit,tags.size());
        PaginationDto<Tag> paginationDto = new PaginationDto<>(pagination,tags);
        return paginationDto;
    }

    public List<Stats> getStats(int limit, int offset) {
        Iterator<Document> result = tagDao.getStats(limit,offset);
        List<Stats> stats = convertDocsIteratorToStats(result);
        return stats;
    }

    public List<Stats> convertDocsIteratorToStats(Iterator<Document> result){
        List<Stats> stats = new ArrayList<>();
        while (result.hasNext()) {
            Document document = result.next();
            stats.add(new Stats(document.get("_id").toString(), document.getInteger("amount")));
        }
        return stats;
    }
}
