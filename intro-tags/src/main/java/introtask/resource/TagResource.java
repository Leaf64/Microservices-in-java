package introtask.resource;

import com.codahale.metrics.annotation.Timed;
import introtask.auth.UserAuth;
import introtask.dto.PaginationDto;
import introtask.dto.Stats;
import introtask.dto.UserTagsDto;
import introtask.entity.Tag;
import introtask.logic.TagLogic;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {

    private TagLogic tagLogic;
    static final Logger LOG = LoggerFactory.getLogger(TagResource.class);
    public TagResource(TagLogic tagLogic) { //ad UserDao userDao
        this.tagLogic = tagLogic;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void create(UserTagsDto userTagsDto) {
        LOG.info("Create userTag with id: "+ userTagsDto.getUserId());
        tagLogic.create(userTagsDto);
    }

    @GET
    @Timed
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTagsByUserId(@Auth UserAuth userAuth,
                                      @PathParam("userId") String userId,
                                      @QueryParam("tagId") String tagId,
                                      @QueryParam("value") String tagValue,
                                      @QueryParam("limit") @Min(0) @DefaultValue("10") int limit,
                                      @QueryParam("offset") @Min(0) @DefaultValue("0") int offset) {

        LOG.info("Get tags for user with id: " + userId);

        List<Tag> tags = tagLogic.getTagsByUserId(userId, new Tag(tagId, tagValue), limit, offset);
        return Response.ok(tags).build();

    }

    @GET
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTagsByUserId(@Auth UserAuth userAuth,
                                      @QueryParam("userIdList") List<String> userIdList,
                                      @QueryParam("tagId") String tagId,
                                      @QueryParam("value") String tagValue,
                                      @QueryParam("limit") @Min(0) @DefaultValue("10") int limit,
                                      @QueryParam("offset") @Min(0) @DefaultValue("0") int offset) {

        LOG.info("Get tags for list of users");
        HashMap<String, PaginationDto<Tag>> map = tagLogic.getTagsForManyId(userIdList,new Tag(tagId, tagValue),limit,offset );
        return Response.ok(map).build();
    }

    @GET
    @Timed
    @Path("/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(@Auth UserAuth userAuth,
                                    @QueryParam("limit") @Min(0) @DefaultValue("10") int limit,
                                    @QueryParam("offset") @Min(0) @DefaultValue("0") int offset) {

        LOG.info("Get statistics");
        List<Stats> stats = tagLogic.getStats(limit,offset);
        return Response.ok(stats).build();
    }
}
