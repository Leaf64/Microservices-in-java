package introtask.logic;


import introtask.dao.UserDao;
import introtask.dto.UserDto;
import introtask.entity.UserEntity;
import introtask.dto.PaginationDto;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class UserLogicTest {

    @Mock
    private UserDao userDao;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private UserLogic userLogic;

    private UserEntity userEntity;
    private UserDto userDto;

    private final static UUID ID = UUID.randomUUID();
    private final static UUID NON_EXISTING_ID = UUID.randomUUID();
    private final static String SAME_EMAIL = "test2@gmail.com";
    private final static String DATE = new Date().toString();

    @Before
    public void setUp() {
         userDto = createUserDto();
         userEntity = new ModelMapper().map(userDto, UserEntity.class);
        userEntity.set_id(new ObjectId());

        given(userDao.findByEmail(eq(SAME_EMAIL))).willReturn(userEntity);
        given(userDao.findById(eq(ID))).willReturn(userEntity);

        List<UserEntity> list = new ArrayList<UserEntity>();
        list.add(userEntity);
        given(userDao.findUsers(eq(Optional.of(ID)), any(),  any(),  any(),  any(),  any(),  any())).willReturn(list);

        List<UserEntity> emptyList = new ArrayList<UserEntity>();
        given(userDao.findUsers(eq(Optional.of(NON_EXISTING_ID)), any(),  any(),  any(),  any(),  any(),  any())).willReturn(emptyList);

        given(userDao.update(eq(userEntity))).willReturn(userEntity);


    }

    @Test
    public void shouldMapAndSaveUserToDb() {
        given(userDao.findById((UUID) any())).willReturn(userEntity);
       userLogic.createUser(userDto);

    }

    @Test (expected = BadRequestException.class)
    public void shouldNotSaveUserWithSameEmail(){
        userDto.setEmail(SAME_EMAIL);
        userLogic.createUser(userDto);
        verify(userDao, never()).save(any());
    }

   @Test
    public void shouldFindUsers(){
       PaginationDto<UserDto> foundUsers = userLogic.findUsers(Optional.of(ID), Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty());
       verify(userDao).findUsers(eq(Optional.of(ID)),  any(),  any(),  any(),  any(),  any(),  any());

        assertEquals(foundUsers.getResults().get(0).getUuid(), userEntity.getUuid());
        assertEquals(foundUsers.getResults().get(0).getCreationDate(), userEntity.getCreationDate());
        assertEquals(foundUsers.getResults().get(0).getEmail(), userEntity.getEmail());
        assertEquals(foundUsers.getResults().get(0).getFirstName(), userEntity.getFirstname());
        assertEquals(foundUsers.getResults().get(0).getLastName(), userEntity.getLastname());
    }

    @Test (expected = NotFoundException.class)
    public void shouldNotFindUsersIfNotExist(){
        PaginationDto<UserDto> foundUsers = userLogic.findUsers(Optional.of(NON_EXISTING_ID), Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty());
        assertEquals(foundUsers.getResults(), new ArrayList<UserDto>());
    }

    @Test
    public void shouldFindUser(){
        UserDto foundUser = userLogic.findUser(Optional.of(ID), null,null,null);
        verify(userDao).findUsers(eq(Optional.of(ID)),  any(),  any(),  any(),  any(),  any(),  any());

        assertEquals(foundUser.getUuid(), userEntity.getUuid());
        assertEquals(foundUser.getCreationDate(), userEntity.getCreationDate());
        assertEquals(foundUser.getEmail(), userEntity.getEmail());
        assertEquals(foundUser.getFirstName(), userEntity.getFirstname());
        assertEquals(foundUser.getLastName(), userEntity.getLastname());
    }

    @Test (expected = NotFoundException.class)
    public void shouldNotFindUserIfNotExists(){
        UserDto foundUser = userLogic.findUser(Optional.of(NON_EXISTING_ID), null,null,null);
        assertEquals(foundUser, null);
    }

    @Test
    public void shouldUpdateUser(){
        given(userDao.update(any())).willReturn(userEntity);
        UserDto updatedUser = userLogic.updateUser(ID, userDto);
        ArgumentCaptor<UserEntity> userArgument = ArgumentCaptor.forClass(UserEntity.class);
        verify(userDao).update(userArgument.capture()); //zlap argumenty ktore beda uzyte w funkcji userDao.save() i sprawdz czy uzywa tych co powinien
        checkDtoToEntityMapping(userArgument.getValue());
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotUpdateUserIfNotExists(){
        UserDto updatedUser = userLogic.updateUser(NON_EXISTING_ID, userDto);
        assertEquals(updatedUser, null);
    }

    @Test
    public void shouldDeleteUser(){
        userLogic.deleteUser(ID);
        ArgumentCaptor<UserEntity> userArgument = ArgumentCaptor.forClass(UserEntity.class);
        verify(userDao).delete(userArgument.capture());
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotDeleteUserIfNotExists(){
        userLogic.deleteUser(NON_EXISTING_ID);
    }

    private void checkDtoToEntityMapping(UserEntity userEntity) {
        assertEquals(userDto.getUuid(), userEntity.getUuid());
        assertEquals(userDto.getCreationDate(), userEntity.getCreationDate());
        assertEquals(userDto.getEmail(), userEntity.getEmail());
        assertEquals(userDto.getFirstName(), userEntity.getFirstname());
        assertEquals(userDto.getLastName(), userEntity.getLastname());
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUuid(ID);
        userDto.setEmail("test@email.pl");
        userDto.setFirstName("testName");
        userDto.setLastName("testName");
        userDto.setCreationDate(DATE);
        return userDto;
    }


}