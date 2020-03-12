package introtask.util;

import java.time.LocalDate;

public enum Zodiac {

    ARIES(newDate(3, 21), newDate(4, 19), "aries"),
    TAURUS(newDate(4, 20), newDate(5, 20), "taurus"),
    GEMINI(newDate(5, 21), newDate(6, 20), "gemini"),
    CANCER(newDate(6, 21), newDate(7, 22), "cancer"),
    LEO(newDate(7, 23), newDate(8, 22), "leo"),
    VIRGO(newDate(8, 23), newDate(9, 22), "virgo"),
    LIBRA(newDate(9, 23), newDate(10, 22), "libra"),
    SCORPIO(newDate(10, 23), newDate(11, 21), "scorpio"),
    SAGITTARIUS(newDate(11, 22), newDate(12, 21), "sagittarius"),
    CAPRICORN(newDate(12, 22), newDate(1, 19), "capricorn"),
    AQUARIUS(newDate(1, 20), newDate(2, 18), "aquarius"),
    PISCES(newDate(2, 19), newDate(3, 20), "pisces");

    private LocalDate from;
    private LocalDate to;
    private String value;

    Zodiac(LocalDate from, LocalDate to, String value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }

    private static LocalDate newDate(int monthOfYear, int dayOfMonth) {
        return LocalDate.of(1, monthOfYear, dayOfMonth);
    }

    public static Zodiac getZodiac(LocalDate birthday) {
        Zodiac prevZodiac = PISCES;
        for (Zodiac zodiac : values()) {
            if (birthday.getMonthValue() == zodiac.from.getMonthValue()) {
                if (birthday.getDayOfMonth() >= zodiac.from.getDayOfMonth()) {
                    return zodiac;
                } else {
                    return prevZodiac;
                }
            }
            prevZodiac = zodiac;
        }
        return null;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
