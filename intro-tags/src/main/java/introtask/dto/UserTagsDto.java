package introtask.dto;

import introtask.entity.Tag;

import java.util.List;
import java.util.UUID;

public class UserTagsDto {

    private String userId;
    private List<Tag> tags;

    public UserTagsDto(){

    }

    public UserTagsDto(UUID uuid, List<Tag> tags){
        this.userId = userId;
        this.tags = tags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}


