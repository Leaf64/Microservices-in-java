package introtask.resource;

import com.codahale.metrics.annotation.Timed;
import introtask.auth.UserAuth;
import introtask.dao.UserDao;
import introtask.dto.UserDto;
import introtask.entity.Tag;
import introtask.logic.UserLogic;
import introtask.dto.PaginationDto;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserLogic userLogic;
    static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    public UserResource(UserLogic userLogic, UserDao userDao) { //ad UserDao userDao
        this.userLogic = userLogic;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@NotNull @Valid UserDto userDto) {
        LOG.info("Register new user");
        UserDto createdUser = userLogic.createUser(userDto);
        return Response.status(Response.Status.CREATED).entity(createdUser).build();
    }

    @GET
    @Timed
    public Response getUsers(@Auth UserAuth userAuth,
                             @QueryParam("id") Optional<UUID> id,
                             @QueryParam("email") Optional<String> email,
                             @QueryParam("firstName") Optional<String> firstName,
                             @QueryParam("lastName") Optional<String> lastName,
                             @QueryParam("offset") Optional<Integer> offset,
                             @QueryParam("limit") Optional<Integer> limit,
                             @QueryParam("orderBy") Optional<String> order) {
        LOG.info("Get users");
        PaginationDto<UserDto> paginationUser = userLogic.findUsers(id, email, firstName, lastName, offset, limit, order);
        return Response.status(Response.Status.OK).entity(paginationUser).build();
    }

    @GET
    @Timed
    @Path("/{id}")
    public Response getUser(@Auth UserAuth userAuth,
                            @PathParam("id") Optional<UUID> id,
                            @QueryParam("email") Optional<String> email,
                            @QueryParam("firstName") Optional<String> firstName,
                            @QueryParam("lastName") Optional<String> lastName) {
        LOG.info("Get user with uuid: " + id);
        UserDto user = userLogic.findUser(id, email, firstName, lastName);
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @GET
    @Timed
    @Path("/{id}/tags")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTagsForUser(@Auth UserAuth userAuth,
                                   @PathParam("id") String userId) {

        LOGGER.info("get tags for client with id: " + userId);
        PaginationDto<Tag> tags = userLogic.getTagsForClient(userId);
        return Response.status(Response.Status.OK).entity(tags).build();
    }

    @PUT
    @Timed
    @Path("/{id}")
    public Response updateUser(@PathParam("id") UUID id, @NotNull @Valid UserDto userDto) {
        LOG.info("Update user with uuid: " + id);
        UserDto user = userLogic.updateUser(id, userDto);
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @DELETE
    @Timed
    @Path("/{id}")
    public Response deleteUser(@Auth UserAuth userAuth,
                               @PathParam("id") UUID id) {
        LOG.info("Delete user with uuid: " + id);
        userLogic.deleteUser(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
