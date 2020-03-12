package introtask.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateParser {

    static String PATTERN = "dd-MM-yyyy";

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(PATTERN);
        String dateString = dateFormat.format(date);
        return dateString;
    }

    public static Date stringToDate(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(PATTERN);
        Date date = dateFormat.parse(dateString);
        return date;
    }

    public static String localToString(LocalDate local) {
        DateFormat dateFormat = new SimpleDateFormat(PATTERN);
        String dateString = dateFormat.format(local);
        return dateString;
    }

    public static LocalDate stringToLocal(String dateString) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }
}
