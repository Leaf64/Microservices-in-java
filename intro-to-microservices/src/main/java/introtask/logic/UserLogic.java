package introtask.logic;

import introtask.dto.UserTagsDto;
import introtask.external.TagsUser;
import introtask.dao.UserDao;
import introtask.entity.Tag;
import introtask.entity.UserEntity;
import introtask.dto.UserDto;
import introtask.dto.PaginationDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static introtask.error.ErrorMessage.*;
import static java.lang.String.format;

public class UserLogic {


    private ModelMapper mapper = new ModelMapper();
    private UserDao userDao;

    private TagsUser tagsUser;

    static final Logger LOG = LoggerFactory.getLogger(UserLogic.class);

    public UserLogic(UserDao userDao, TagsUser tagsUser) {
        this.userDao = userDao;
        this.tagsUser = tagsUser;
    }

    public UserDto createUser(UserDto userDto) {
        userDto = fillDtoReceivedFromUser(userDto);
        UserEntity userEntity = mapDtoToEntity(userDto);
        saveToDb(userEntity);
        return userDto;
    }

    public PaginationDto<UserDto> findUsers(Optional<UUID> id,
                                            Optional<String> email,
                                            Optional<String> firstName,
                                            Optional<String> lastName,
                                            Optional<Integer> offset,
                                            Optional<Integer> limit,
                                            Optional<String> order) {

        limit = validateLimit(limit);
        offset = validateOffset(offset);
        List<UserEntity> usersEnt = userDao.findUsers(id, email, firstName, lastName, offset, limit, order);
        validateIfUserListEmpty(usersEnt);

        List<UserDto> usersDto = new ArrayList<UserDto>();
        usersDto = mapEntityListToDtoList(usersEnt, usersDto);

        fillUsersTags(usersDto);

        PaginationDto.Pagination paginationIn = new PaginationDto.Pagination(offset.get(), limit.get(), usersDto.size());
        PaginationDto<UserDto> paginationOut = new PaginationDto<UserDto>(paginationIn, usersDto);

        return paginationOut;
    }

    public PaginationDto<UserDto> findUsers(
                                            Optional<Integer> offset,
                                            Optional<Integer> limit,
                                            Optional<String> order) {

        limit = validateLimit(limit);
        offset = validateOffset(offset);
        List<UserEntity> usersEnt = userDao.findUsers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), offset, limit, order);
        validateIfUserListEmpty(usersEnt);
        List<UserDto> usersDto = new ArrayList<UserDto>();
        usersDto = mapEntityListToDtoList(usersEnt, usersDto);
        fillUsersTags(usersDto);
        PaginationDto.Pagination paginationIn = new PaginationDto.Pagination(offset.get(), limit.get(), usersDto.size());
        PaginationDto<UserDto> paginationOut = new PaginationDto<UserDto>(paginationIn, usersDto);
        return paginationOut;
    }

    public UserDto findUser(Optional<UUID> id,
                            Optional<String> email,
                            Optional<String> firstName,
                            Optional<String> lastName) {
        List<UserEntity> usersEnt = userDao.findUsers(id, email, firstName, lastName, Optional.empty(), Optional.of(1), Optional.empty());
        validateIfUserListEmpty(usersEnt);
        UserDto userDto = mapEntityToDto(usersEnt.get(0));
        fillUserTags(userDto);
        return userDto;
    }


    public UserDto updateUser(UUID id, UserDto userDto) {
        UserEntity oldUser = userDao.findById(id);
        validateIfUserFound(oldUser);

        UserEntity newUser = mapDtoToEntity(userDto);
        newUser.setUuid(id);
        newUser.set_id(oldUser.get_id());

        //if email is the same, because we want to change other params, we let it go, but if not:
        validateIfSameEmailIsSameUuid(newUser);

        UserEntity result = userDao.update(newUser);
        return mapEntityToDto(result);
    }

    public Boolean deleteUser(UUID id) {
        UserEntity user = userDao.findById(id);
        validateIfUserFound(user);
        userDao.delete(user);
        return true;
    }


    public PaginationDto<UserDto> findUsersWithBirthday(
                                            Optional<Integer> offset,
                                            Optional<Integer> limit,
                                            Optional<String> order) {

        limit = validateLimit(limit);
        offset = validateOffset(offset);
        List<UserEntity> usersEnt = userDao.findUsersWithBirthday(offset, limit, order);
        validateIfUserListEmpty(usersEnt);

        List<UserDto> usersDto = new ArrayList<UserDto>();
        usersDto = mapEntityListToDtoList(usersEnt, usersDto);

        fillUsersTags(usersDto);

        PaginationDto.Pagination paginationIn = new PaginationDto.Pagination(offset.get(), limit.get(), usersDto.size());
        PaginationDto<UserDto> paginationOut = new PaginationDto<UserDto>(paginationIn, usersDto);

        return paginationOut;
    }

    //=================================

    private void saveToDb(UserEntity userEntity) {
        try {
            userDao.save(userEntity);
        } catch (DuplicateKeyException ex) {
            LOG.error("Email " + userEntity.getEmail() + "is duplicated, request rejected");
            throw new BadRequestException(format(USER_ALREADY_EXISTS, userEntity.getEmail()));
        }
    }

    private void validateIfUserFound(UserEntity userEntity) {
        if (userEntity == null) {
            LOG.info("Didn't find any users");
            throw new NotFoundException(format(USER_NOT_FOUND));
        }
    }


    private UserDto mapEntityToDto(UserEntity userEntity) {
        return mapper.map(userEntity, UserDto.class);
    }

    private UserEntity mapDtoToEntity(UserDto userDto) {
        return mapper.map(userDto, UserEntity.class);
    }

    private List<UserDto> mapEntityListToDtoList(List<UserEntity> entities, List<UserDto> dtos) {
        for (UserEntity userEnt : entities) {
            dtos.add(mapEntityToDto(userEnt));
        }
        return dtos;
    }

    private UserDto fillDtoReceivedFromUser(UserDto userDto) {
        userDto.setUuid(UUID.randomUUID());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(new Date());
        userDto.setCreationDate(date);

        return userDto;
    }

    private boolean validateIfUserListEmpty(List<UserEntity> usersEnt) {
        if (usersEnt.isEmpty()) {
            LOG.info("Didn't find any users");
            throw new NotFoundException(format(USER_NOT_FOUND));
        }
        return true;
    }

    private Optional<Integer> validateLimit(Optional<Integer> limit) {
        //because for Morphia limit 0 means 'return everything'
        if (!limit.isPresent() || limit.get().equals(0)) {
            LOG.info("Given limit uncorrect, set 10");
            limit = Optional.of(10);
        }
        if (limit.get() > 15) {
            LOG.info("Given limit too big, set 15");
            limit = Optional.of(15);
        }
        return limit;
    }

    private Optional<Integer> validateOffset(Optional<Integer> offset) {
        if (!offset.isPresent()) {
            offset = Optional.of(0);
        }
        return offset;
    }

    private void validateIfSameEmailIsSameUuid(UserEntity userEntity) {
        if (!userEntity.getUuid().equals(userDao.findByEmail(userEntity.getEmail()).getUuid())) {
            throw new BadRequestException(format(USER_ALREADY_EXISTS, userEntity.getEmail()));
        }
    }

    public PaginationDto<Tag> getTagsForClient(String userId) {
        try {
            List<String> uuids = new ArrayList<>();
            uuids.add(userId);
            HashMap<String, PaginationDto<Tag>> map = tagsUser.getTagsForUsersList(uuids);
            System.out.println("MAPKA: " + map.get(0).getResults().get(0).getTagId());
            return map.get(0);


            // return tagsUser.getTagsByUserId(userId);
        } catch (feign.FeignException.Unauthorized ex) {
            throw new NotAuthorizedException(WRONG_CRED_TAG_API, Response.Status.UNAUTHORIZED);
        }
    }

    private UserDto fillUserTags(UserDto userDto) {
        List<UserDto> dto = new ArrayList<>();
        dto.add(userDto);
        dto = fillUsersTags(dto);

        // PaginationDto<Tag> tags = getTagsForClient(userDto.getUuid().toString());
        // userDto.setTags(tags);
        return dto.get(0);
    }

    private List<UserDto> fillUsersTags(List<UserDto> userDtos) {
        if(userDtos.isEmpty()){
            return userDtos;
        }
        List<String> uuids = makeListOfId(userDtos);
        HashMap<String, PaginationDto<Tag>> userTagsMap = getTagsMap(uuids);
        userDtos = inputTagsIntoDtos(userDtos, userTagsMap);
        return userDtos;
    }

    private List<String> makeListOfId(List<UserDto> userDtos) {
        List<String> uuids = new ArrayList<String>();
        for (UserDto userDto : userDtos) {
            uuids.add(userDto.getUuid().toString());
        }
        return uuids;
    }

    private HashMap<String, PaginationDto<Tag>> getTagsMap(List<String> uuids) {
        HashMap<String, PaginationDto<Tag>> userTagsMap = new HashMap<String, PaginationDto<Tag>>();
        if(uuids.isEmpty()){
            return null;
        }
        try {
            userTagsMap = tagsUser.getTagsForUsersList(uuids);
        } catch (feign.FeignException.Unauthorized ex) {
            throw new NotAuthorizedException(WRONG_CRED_TAG_API, Response.Status.UNAUTHORIZED);
        }
        return userTagsMap;
    }

    private List<UserDto> inputTagsIntoDtos(List<UserDto> userDtos, HashMap<String, PaginationDto<Tag>> userTagsMap) {
        for (UserDto userDto : userDtos) {
            userDto.setTags(userTagsMap.get(userDto.getUuid().toString()));
        }
        return userDtos;
    }

    public void addUserTags(UUID userId, List<Tag> newTags) {
        UserTagsDto newUserTags = new UserTagsDto(userId.toString(), newTags);
        tagsUser.create(newUserTags);
    }
}


