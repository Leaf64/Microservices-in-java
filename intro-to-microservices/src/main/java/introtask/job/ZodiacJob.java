package introtask.job;

import introtask.dao.UserDao;
import introtask.dto.PaginationDto;
import introtask.dto.UserDto;
import introtask.entity.Tag;
import introtask.logic.UserLogic;
import introtask.util.Zodiac;
import introtask.util.DateParser;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static introtask.error.ErrorMessage.WRONG_CRED_TAG_API;

public class ZodiacJob implements Job {

    static final Logger LOG = LoggerFactory.getLogger(ZodiacJob.class);
    private String ZODIAC = "zodiac";

    private UserLogic userLogic;

    public ZodiacJob() {

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        userLogic = getUserLogicFromContext(context);
        validateUserLogic(userLogic);
        LOG.info("Beginning Zodiac Job");

        List<UserDto> users;
        int i = 0;
        int numberOfDocs = 10;
        do {
            try {
                users = userLogic.findUsersWithBirthday(Optional.of(i * numberOfDocs), Optional.of(numberOfDocs), Optional.of("_id")).getResults();
            } catch (NotFoundException ex) {
                return;
            }
            processUsers(users);
            i++;
        } while (users.size() != 0);
        LOG.info("Finished Zodiac Job");
    }

    private void validateUserLogic(UserLogic userLogic) throws JobExecutionException {
        if (userLogic == null) {
            throw new JobExecutionException("Didn't get userLogic from context in ZodiacJob ");
        }
    }

    public void processUsers(List<UserDto> users) {
        if (users.isEmpty()) {
            return;
        }
        for (UserDto user : users) {
            try {
                updateZodiac(user);
            } catch (ParseException e) {
                LOG.error("Parse exception in class Zodiac Job function updateZodiac");
            }
        }
    }

    private void updateZodiac(UserDto user) throws ParseException {
        Tag zodiacTag = getZodiacFromTags(user.getTags());
        if (zodiacTag == null) {
            LOG.info("Setting zodiac tag for user: " + user.getUuid());
            setZodiac(user);
        }
    }

    private Tag getZodiacFromTags(PaginationDto<Tag> tagsPg) {
        if (tagsPg.getPagination().getTotalCount() == 0) {
            return null;
        }
        List<Tag> tags = tagsPg.getResults();
        for (Tag tag : tags) {
            if (tag.getTagId().equals(ZODIAC)) {
                return tag;
            }
        }
        return null;
    }

    private void setZodiac(UserDto user) throws ParseException {

        LocalDate birthday = DateParser.stringToLocal(user.getDateOfBirth());
        String zodiacValue = Zodiac.getZodiac(birthday).getValue();
        Tag zodiacTag = new Tag(ZODIAC, zodiacValue);
        List<Tag> tags = new ArrayList<>();
        tags.add(zodiacTag);
        userLogic.addUserTags(user.getUuid(), tags);
    }

    private UserLogic getUserLogicFromContext(JobExecutionContext context) throws JobExecutionException {
        UserLogic userLogic = null;
        try {
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            userLogic = (UserLogic) schedulerContext.get("userLogic");
        } catch (SchedulerException ex) {
            LOG.error("Error during getting UserLogic from context in ZodiacJob");
            throw new JobExecutionException("Error during getting UserLogic from context in ZodiacJob");
        }
        return userLogic;
    }
}
