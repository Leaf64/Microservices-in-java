package introtask.job;

import introtask.dao.UserDao;
import introtask.entity.UserEntity;
import introtask.logic.UserLogic;
import introtask.resource.UserResource;
import introtask.util.DateParser;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class BirthdayJob implements Job {

    static final Logger LOG = LoggerFactory.getLogger(BirthdayJob.class);
    private UserDao userDao;

    public BirthdayJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        userDao = getUserDaoFromContext(context);
        validateUserDao(userDao);
        LOG.info("Beginning Birthday Job");

        List<UserEntity> users;
        int i = 0;
        int numberOfDocs = 10;
        do {
            try {
                users = userDao.findUsersWithoutBirthday(Optional.of(i * numberOfDocs), Optional.of(numberOfDocs), Optional.of("_id"));
            } catch (NotFoundException ex) {
                return;
            }
            processUsers(users);
            i++;
        } while (users.size() != 0);

        LOG.info("Finished Birthday Job");
    }

    private void validateUserDao(UserDao userDao) throws JobExecutionException {
        if (userDao == null) {
            throw new JobExecutionException("Didn't get userDao from context in BirthdayJob ");
        }
    }

    private void processUsers(List<UserEntity> users) {
        for (UserEntity user : users) {
            try {
                setBirthday(user);
            } catch (ParseException e) {
                LOG.error("Parse exception in class BirthdayJob function setBirthday");
            }
        }
    }

    private void setBirthday(UserEntity user) throws ParseException {
        LOG.info("Setting date of birth of user: " + user.getUuid());

        Date date = DateParser.stringToDate(user.getCreationDate());
        Date birthday = generateBirthdayFromDate(date);
        String birthString = DateParser.dateToString(birthday);
        user.setDateOfBirth(birthString);
        userDao.update(user);
    }

    private Date generateBirthdayFromDate(Date date) {
        int year = drawField(date.getYear(), 18, 40);
        int month = Math.abs(drawField(date.getMonth(), 1, 12));

        int maxDay = (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) ? 31 : 30;
        if (month == 2) {
            if (year % 4 == 0) {
                maxDay = 29;
            } else {
                maxDay = 28;
            }
        }

        int day = Math.abs(drawField(date.getDay(), 1, maxDay));

        Date birthday = new Date(year, month, day);
        return birthday;
    }

    private int drawField(int first, int from, int to) {
        return first - ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    private UserDao getUserDaoFromContext(JobExecutionContext context) throws JobExecutionException {
        UserDao userDao = null;
        try {
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            userDao = (UserDao) schedulerContext.get("userDao");
        } catch (SchedulerException ex) {
            LOG.error("SchedulerException in Birthday Job, function getUserDaoFromContext");
            throw new JobExecutionException("SchedulerException in Birthday Job, function getUserDaoFromContext");
        }
        return userDao;
    }

}
