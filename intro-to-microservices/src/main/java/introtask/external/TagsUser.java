package introtask.external;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import introtask.dto.PaginationDto;
import introtask.dto.UserTagsDto;
import introtask.entity.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface TagsUser {

    @RequestLine("POST tags")
    @Headers("Content-Type: application/json")
    void create(UserTagsDto userTagsDTO);


    @RequestLine("GET tags/{userId}?tagId={tagId}&value={value}&limit={limit}&offset={offset}")
    PaginationDto<Tag> getTagsByUserId(@Param("userId") String userId);

    @RequestLine("GET tags/?userIdList={userIdList}&tagId={tagId}&value={value}&limit={limit}&offset={offset}")
    HashMap<String, PaginationDto<Tag>> getTagsForUsersList(@Param("userIdList")List<String> userIdList);
}
