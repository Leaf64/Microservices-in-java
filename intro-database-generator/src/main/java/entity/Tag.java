package entity;

public class Tag {

    private String tagId;
    private String value;

    public Tag() {

    }

    public Tag(String tagId, String value) {
        this.tagId = tagId;
        this.value = value;
    }

    public Tag(UserTagEntity userTagEntity) {
        this.tagId = userTagEntity.getTag().tagId;
        this.value = value;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
