package entity;


import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.UUID;

@Entity(value = "tags", noClassnameStored = true)
@Indexes({@Index(fields = {@Field("userId")}, options = @IndexOptions(unique = false)),
        @Index(fields = {@Field("userId"), @Field("tag.tagId")}, options = @IndexOptions(unique = true))})
public class UserTagEntity {

    @Id
    private ObjectId id;

    private UUID userId;
    private Tag tag;

    public UserTagEntity() {

    }

    public UserTagEntity(UUID clientId, Tag tag) {
        this.userId = clientId;
        this.tag = tag;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;

    }
}
