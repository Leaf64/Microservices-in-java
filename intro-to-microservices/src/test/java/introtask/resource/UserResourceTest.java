package introtask.resource;

import introtask.auth.UserAuth;
import introtask.dto.PaginationDto;
import introtask.dto.UserDto;
import introtask.dto.UserTagsDto;
import introtask.entity.Tag;
import introtask.entity.UserEntity;
import introtask.logic.UserLogic;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

    @Mock
    private UserLogic userLogic;
    @Mock
    private UserEntity userEntity;
    @InjectMocks
    private UserResource userResource;

    private UserDto userDto;
    private UserAuth auth;
    private static final UUID ID = UUID.randomUUID();
    private static final String DATE = new Date().toString();

    @Before
    public void setUp(){
        userDto = new UserDto(ID, "Testmail@gmail.com", "TestName","Testname", DATE);//
        given(userEntity.getUuid()).willReturn(ID); //
        given(userLogic.findUser(eq(Optional.of(ID)), any(), any(),any())).willReturn(userDto);//
        auth = new UserAuth("login", "passw");
        //List<UserDto> list = new ArrayList<UserDto>();
        //list.add(userDto);
        //PaginationDto.Pagination paginationIn = new PaginationDto.Pagination(1,1,1);
       // PaginationDto<UserDto> paginationOut = new PaginationDto<UserDto>(paginationIn, list);
       // given(userLogic.findUsers(eq(Optional.of(ID)), any(), any(),any(),any(),any(),any())).willReturn(paginationOut);
    }

    @Test
    public void shouldCreateUser(){
        Response response = userResource.createUser(userDto);
        verify(userLogic).createUser(userDto);
        Assert.assertEquals(201, response.getStatus());
    }
    @Test
    public void shouldFindUsersById(){
        Response response = userResource.getUsers(auth, Optional.of(ID), null,null,null,null,null, null);
        verify(userLogic).findUsers(eq(Optional.of(ID)),any(), any(),any(),any(),any(),any());
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldFindUserById(){
        Response response =  userResource.getUser(auth,Optional.of(ID), Optional.empty(), Optional.empty(),Optional.empty());
        verify(userLogic).findUser(eq(Optional.of(ID)),any(), any(),any());
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldUpdateUser(){
        Response response = userResource.updateUser(ID, userDto);
        verify(userLogic).updateUser(eq(ID), eq(userDto));
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldDeleteUser(){
        Response response = userResource.deleteUser(auth,ID);
        verify(userLogic).deleteUser(ID);
        Assert.assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldNotFindTagsForUser(){
        Response response = userResource.getTagsForUser(auth,ID.toString());
        verify(userLogic).getTagsForClient(ID.toString());
        List<Tag> tags = (List<Tag>)response.getEntity();
        Assert.assertEquals(tags.size(), 0);
    }

    @Test
    public void shouldFindTagsForUser(){
        List<Tag> list = new ArrayList<Tag>();
        list.add(new Tag("123","walju"));
        list.add(new Tag("456","sralju"));
        UserTagsDto tag = new UserTagsDto(ID.toString(), list);

        given(userLogic.getTagsForClient(eq(ID.toString()))).willReturn((PaginationDto<Tag>) list);

        Response response = userResource.getTagsForUser(auth,ID.toString());
        verify(userLogic).getTagsForClient(ID.toString());
        List<Tag> tags = (List<Tag>)response.getEntity();
        Assert.assertNotEquals(tags.size(), 0);
    }
}
